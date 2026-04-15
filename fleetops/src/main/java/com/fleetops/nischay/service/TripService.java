package com.fleetops.nischay.service;

import com.fleetops.nischay.aop.TrackExecution;
import com.fleetops.nischay.assignment.AssignmentService;
import com.fleetops.nischay.driver.Driver;
import com.fleetops.nischay.driver.DriverStatus;
import com.fleetops.nischay.exception.BusinessRuleException;
import com.fleetops.nischay.exception.ResourceNotFoundException;
import com.fleetops.nischay.fleet.Vehicle;
import com.fleetops.nischay.fleet.VehicleStatus;
import com.fleetops.nischay.locking.DistributedLockManager;
import com.fleetops.nischay.repository.DriverRepository;
import com.fleetops.nischay.repository.TripRepository;
import com.fleetops.nischay.repository.VehicleRepository;
import com.fleetops.nischay.trip.Trip;
import com.fleetops.nischay.trip.TripStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {

    private final TripRepository tripRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final AssignmentService assignmentService;
    private final DistributedLockManager lockManager;

    private final @Qualifier("tripExecutor") ExecutorService executor;

    @TrackExecution
    @Transactional
    public Trip createTrip(String source, String destination) {

        Trip trip = Trip.builder()
                .source(source)
                .destination(destination)
                .status(TripStatus.CREATED)
                .startTime(Instant.now())
                .build();

        trip = tripRepository.save(trip);

        final Long tripId = trip.getId();

        // Async assignment — runs outside this transaction
        executor.submit(() -> {
            try {
                assignDriverAndVehicle(tripId);
            } catch (Exception e) {
                log.error("Async trip assignment failed | tripId={}", tripId, e);
            }
        });

        return trip;
    }

    @Transactional
    public void assignDriverAndVehicle(Long tripId) {

        int maxRetries = 3;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                Trip trip = tripRepository.findById(tripId)
                        .orElseThrow(() -> new ResourceNotFoundException("Trip", tripId));

                if (trip.getStatus() != TripStatus.CREATED) {
                    log.info("Trip {} already in status {}, skipping assignment", tripId, trip.getStatus());
                    return;
                }

                Driver driver = assignmentService.getAvailableDriver();
                Vehicle vehicle = assignmentService.getAvailableVehicle();

                String lockKey = "driver:" + driver.getId();
                String owner = lockManager.acquireLock(lockKey);

                if (owner == null) {
                    log.warn("Could not acquire lock for driver {} on attempt {}", driver.getId(), attempt);
                    Thread.sleep(200L * attempt);
                    continue;
                }

                try {
                    // Re-check driver status under lock
                    Driver freshDriver = driverRepository.findById(driver.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Driver", driver.getId()));

                    if (freshDriver.getStatus() != DriverStatus.AVAILABLE) {
                        log.warn("Driver {} no longer available, retrying...", driver.getId());
                        continue;
                    }

                    freshDriver.setStatus(DriverStatus.ON_TRIP);
                    freshDriver.setAssignedVehicle(vehicle);
                    driverRepository.save(freshDriver);

                    vehicle.setStatus(VehicleStatus.IN_TRIP);
                    vehicleRepository.save(vehicle);

                    trip.setDriver(freshDriver);
                    trip.setVehicle(vehicle);
                    trip.setStatus(TripStatus.ASSIGNED);
                    tripRepository.save(trip);

                    assignmentService.invalidateDriverCache();

                    log.info("Trip assigned | tripId={} driverId={} vehicleId={}",
                            tripId, freshDriver.getId(), vehicle.getId());
                    return;

                } finally {
                    lockManager.releaseLock(lockKey, owner);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Assignment interrupted for tripId={}", tripId);
                break;
            } catch (ResourceNotFoundException e) {
                throw e;
            } catch (Exception e) {
                log.warn("Assignment attempt {} failed for tripId={}: {}", attempt, tripId, e.getMessage());
                if (attempt == maxRetries) {
                    Trip trip = tripRepository.findById(tripId).orElse(null);
                    if (trip != null && trip.getStatus() == TripStatus.CREATED) {
                        trip.setStatus(TripStatus.CANCELLED);
                        trip.setCancellationReason("No resources available after " + maxRetries + " attempts");
                        tripRepository.save(trip);
                        log.error("Trip cancelled due to assignment failure | tripId={}", tripId);
                    }
                }
            }
        }
    }

    @Transactional
    public Trip completeTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", tripId));

        if (trip.getStatus() != TripStatus.ASSIGNED && trip.getStatus() != TripStatus.IN_PROGRESS) {
            throw new BusinessRuleException("Cannot complete trip in status: " + trip.getStatus());
        }

        trip.setStatus(TripStatus.COMPLETED);
        trip.setEndTime(Instant.now());

        // Release driver and vehicle
        if (trip.getDriver() != null) {
            Driver driver = trip.getDriver();
            driver.setStatus(DriverStatus.AVAILABLE);
            driver.setAssignedVehicle(null);
            driverRepository.save(driver);
        }

        if (trip.getVehicle() != null) {
            Vehicle vehicle = trip.getVehicle();
            vehicle.setStatus(VehicleStatus.ACTIVE);
            vehicleRepository.save(vehicle);
        }

        assignmentService.invalidateDriverCache();

        log.info("Trip completed | tripId={}", tripId);
        return tripRepository.save(trip);
    }

    @Transactional
    public Trip cancelTrip(Long tripId, String reason) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", tripId));

        if (trip.getStatus() == TripStatus.COMPLETED || trip.getStatus() == TripStatus.CANCELLED) {
            throw new BusinessRuleException("Cannot cancel trip in status: " + trip.getStatus());
        }

        trip.setStatus(TripStatus.CANCELLED);
        trip.setEndTime(Instant.now());
        trip.setCancellationReason(reason != null ? reason : "Cancelled by user");

        // Release resources
        if (trip.getDriver() != null) {
            Driver driver = trip.getDriver();
            driver.setStatus(DriverStatus.AVAILABLE);
            driver.setAssignedVehicle(null);
            driverRepository.save(driver);
        }

        if (trip.getVehicle() != null) {
            Vehicle vehicle = trip.getVehicle();
            vehicle.setStatus(VehicleStatus.ACTIVE);
            vehicleRepository.save(vehicle);
        }

        assignmentService.invalidateDriverCache();

        log.info("Trip cancelled | tripId={} reason={}", tripId, reason);
        return tripRepository.save(trip);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPS_MANAGER') or @tripSecurityService.isDriverOwner(#driverId, authentication)")
    @Transactional(readOnly = true)
    public List<Trip> getTripsForDriver(Long driverId) {
        return tripRepository.findByDriverId(driverId);
    }
}