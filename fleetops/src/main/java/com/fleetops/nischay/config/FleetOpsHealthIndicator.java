package com.fleetops.nischay.config;

import com.fleetops.nischay.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FleetOpsHealthIndicator implements HealthIndicator {

    private final TripRepository tripRepository;

    @Override
    public Health health() {
        try {
            long count = tripRepository.count();
            return Health.up()
                    .withDetail("totalTrips", count)
                    .withDetail("service", "FleetOps")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}