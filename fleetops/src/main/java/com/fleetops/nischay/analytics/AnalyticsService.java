package com.fleetops.nischay.analytics;

import com.fleetops.nischay.aop.TrackExecution;
import com.fleetops.nischay.dto.response.DeliveryStatsResponse;
import com.fleetops.nischay.repository.TripRepository;
import com.fleetops.nischay.trip.TripStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TripRepository tripRepository;
    private final @Qualifier("analyticsExecutor") ExecutorService executor;

    @TrackExecution
    public DeliveryStatsResponse getDeliveryStats() {

        CompletableFuture<Long> totalFuture =
                CompletableFuture.supplyAsync(tripRepository::count, executor);

        CompletableFuture<Long> completedFuture =
                CompletableFuture.supplyAsync(
                        () -> tripRepository.countByStatus(TripStatus.COMPLETED), executor);

        CompletableFuture<Long> cancelledFuture =
                CompletableFuture.supplyAsync(
                        () -> tripRepository.countByStatus(TripStatus.CANCELLED), executor);

        try {
            CompletableFuture.allOf(totalFuture, completedFuture, cancelledFuture)
                    .get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Analytics query timed out or failed", e);
            throw new RuntimeException("Failed to fetch delivery stats", e);
        }

        long total = totalFuture.join();
        long completed = completedFuture.join();
        long cancelled = cancelledFuture.join();

        return DeliveryStatsResponse.builder()
                .totalTrips(total)
                .completedTrips(completed)
                .cancelledTrips(cancelled)
                .completionRate(total > 0 ? (double) completed / total * 100 : 0)
                .cancellationRate(total > 0 ? (double) cancelled / total * 100 : 0)
                .build();
    }
}