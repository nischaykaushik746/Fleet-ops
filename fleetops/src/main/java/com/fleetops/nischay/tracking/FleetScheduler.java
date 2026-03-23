package com.fleetops.nischay.tracking;

import com.fleetops.nischay.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FleetScheduler {

    private final TripRepository tripRepository;

    @Scheduled(fixedRate = 10000)
    public void monitorTrips() {

        System.out.println("Monitoring active trips...");

        tripRepository.findAll().forEach(trip -> {
            if (trip.getStatus().name().equals("IN_PROGRESS")) {
                System.out.println("Tracking trip: " + trip.getId());
            }
        });
    }
}