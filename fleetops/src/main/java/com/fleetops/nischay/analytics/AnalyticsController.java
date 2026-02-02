package com.fleetops.nischay.analytics;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/deliveries")
    @PreAuthorize("hasAnyRole('ADMIN','OPS_MANAGER')")
    public DeliveryStatsDto stats() {
        return analyticsService.getDeliveryStats();
    }
}
