package com.fleetops.nischay.service;

import com.fleetops.nischay.aop.TrackExecution;
import com.fleetops.nischay.assignment.AssignmentService;
import com.fleetops.nischay.driver.Driver;
import com.fleetops.nischay.driver.DriverStatus;
import com.fleetops.nischay.fleet.Vehicle;
import com.fleetops.nischay.fleet.VehicleStatus;
import com.fleetops.nischay.locking.DistributedLockManager;
import com.fleetops.nischay.locking.DriverLockManager;
import com.fleetops.nischay.repository.TripRepository;
import com.fleetops.nischay.trip.Trip;
import com.fleetops.nischay.trip.TripStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {

    private final TripRepository tripRepository;
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
                .startTime(LocalDateTime.now())
                .build();

        trip = tripRepository.save(trip);

        Trip finalTrip = trip;

        executor.submit(() -> {
            try {
                assignDriverAndVehicle(finalTrip);
            } catch (Exception e) {
                log.error("Trip assignment failed for tripId={}", finalTrip.getId(), e);
            }
        });

        return trip;
    }

    @TrackExecution
    public void assignDriverAndVehicle(Trip trip) {

        int retries = 3;

        while (retries-- > 0) {
            try {

                Driver driver = assignmentService.getAvailableDriver();
                Vehicle vehicle = assignmentService.getAvailableVehicle();

                String lockKey = "driver:" + driver.getId();

                String owner = lockManager.acquireLock(lockKey);

                if (owner != null) {

                    try {

                        if (driver.getStatus() != DriverStatus.AVAILABLE) continue;

                        driver.setStatus(DriverStatus.ON_TRIP);
                        vehicle.setStatus(VehicleStatus.IN_TRIP);

                        trip.setDriver(driver);
                        trip.setVehicle(vehicle);
                        trip.setStatus(TripStatus.ASSIGNED);

                        tripRepository.save(trip);

                        assignmentService.invalidateDriverCache();

                        log.info("Trip assigned with distributed lock | tripId={}", trip.getId());

                        return;

                    } finally {
                        lockManager.releaseLock(lockKey, owner);
                    }
                }

            } catch (Exception e) {
                log.warn("Retrying distributed assignment...");
            }
        }

        trip.setStatus(TripStatus.CANCELLED);
        tripRepository.save(trip);

        log.error("Trip failed (distributed locking)");
    }

    @PreAuthorize("hasRole('ADMIN') OR #driverId == authentication.principal.id")
    public List<Trip> getTripsForDriver(Long driverId) {
        return tripRepository.findAll();
    }
}