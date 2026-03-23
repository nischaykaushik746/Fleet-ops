package com.fleetops.nischay.service;

import com.fleetops.nischay.assignment.AssignmentService;
import com.fleetops.nischay.driver.Driver;
import com.fleetops.nischay.driver.DriverStatus;
import com.fleetops.nischay.fleet.Vehicle;
import com.fleetops.nischay.fleet.VehicleStatus;
import com.fleetops.nischay.locking.DriverLockManager;
import com.fleetops.nischay.repository.TripRepository;
import com.fleetops.nischay.trip.Trip;
import com.fleetops.nischay.trip.TripStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final AssignmentService assignmentService;

    private final @Qualifier("tripExecutor") ExecutorService executorService;

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
        executorService.submit(() -> assignDriverAndVehicle(finalTrip));

        return trip;
    }

    private void assignDriverAndVehicle(Trip trip) {

        int retries = 3;

        while (retries-- > 0) {
            try {

                Driver driver = assignmentService.getAvailableDriver();
                Vehicle vehicle = assignmentService.getAvailableVehicle();

                var lock = DriverLockManager.getLock(driver.getId());

                if (lock.tryLock()) {
                    try {

                        if (driver.getStatus() != DriverStatus.AVAILABLE) continue;

                        driver.setStatus(DriverStatus.ON_TRIP);
                        vehicle.setStatus(VehicleStatus.IN_TRIP);

                        trip.setDriver(driver);
                        trip.setVehicle(vehicle);
                        trip.setStatus(TripStatus.ASSIGNED);

                        tripRepository.save(trip);
                        return;

                    } finally {
                        lock.unlock();
                    }
                }

            } catch (Exception e) {
                System.out.println("Retrying assignment...");
            }
        }

        throw new RuntimeException("Assignment failed");
    }

    @PreAuthorize("hasRole('ADMIN') OR #driverId == authentication.principal.id")
    public List<Trip> getTripsForDriver(Long driverId) {
        return tripRepository.findAll();
    }
}