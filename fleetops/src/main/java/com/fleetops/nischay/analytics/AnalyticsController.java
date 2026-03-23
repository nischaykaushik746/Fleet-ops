package com.fleetops.nischay.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/deliveries")
    @PreAuthorize("hasAnyRole('ADMIN','OPS_MANAGER')")
    public DeliveryStatsDto getStats() {
        return analyticsService.getDeliveryStats();
    }
}