package com.fleetops.nischay.analytics;

import com.fleetops.nischay.aop.TrackExecution;
import com.fleetops.nischay.repository.TripRepository;
import com.fleetops.nischay.trip.TripStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TripRepository tripRepository;

    private final @Qualifier("analyticsExecutor") ExecutorService executor;

    @TrackExecution
    public DeliveryStatsDto getDeliveryStats() {

        CompletableFuture<Long> total =
                CompletableFuture.supplyAsync(tripRepository::count, executor);

        CompletableFuture<Long> completed =
                CompletableFuture.supplyAsync(() ->
                        (long) tripRepository.findByStatus(TripStatus.COMPLETED).size(), executor);

        CompletableFuture<Long> cancelled =
                CompletableFuture.supplyAsync(() ->
                        (long) tripRepository.findByStatus(TripStatus.CANCELLED).size(), executor);

        CompletableFuture.allOf(total, completed, cancelled).join();

        DeliveryStatsDto dto = new DeliveryStatsDto();
        dto.setTotalTrips(total.join());
        dto.setCompletedTrips(completed.join());
        dto.setCancelledTrips(cancelled.join());

        return dto;
    }
}