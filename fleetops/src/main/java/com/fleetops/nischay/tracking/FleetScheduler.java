package com.fleetops.nischay.tracking;

import com.fleetops.nischay.idempotency.IdempotencyService;
import com.fleetops.nischay.repository.RefreshTokenRepository;
import com.fleetops.nischay.repository.TripRepository;
import com.fleetops.nischay.security.TokenBlacklistService;
import com.fleetops.nischay.trip.TripStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FleetScheduler {

    private final TripRepository tripRepository;
    private final TokenBlacklistService tokenBlacklistService;
    private final IdempotencyService idempotencyService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(fixedRate = 10000)
    public void monitorActiveTrips() {
        var activeTrips = tripRepository.findByStatusIn(
                List.of(TripStatus.ASSIGNED, TripStatus.IN_PROGRESS));

        if (!activeTrips.isEmpty()) {
            log.info("Active trips: {}", activeTrips.size());
            activeTrips.forEach(trip ->
                    log.debug("Tracking trip: id={} status={} driver={}",
                            trip.getId(), trip.getStatus(),
                            trip.getDriver() != null ? trip.getDriver().getId() : "unassigned"));
        }
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void cleanupExpiredTokens() {
        log.debug("Cleaning up expired tokens and idempotency keys...");
        tokenBlacklistService.cleanup();
        idempotencyService.cleanup();
    }

    @Scheduled(cron = "0 0 2 * * *") // Every day at 2 AM
    @Transactional
    public void cleanupExpiredRefreshTokens() {
        log.info("Cleaning up expired refresh tokens...");
        refreshTokenRepository.deleteExpiredAndRevoked(Instant.now());
    }
}