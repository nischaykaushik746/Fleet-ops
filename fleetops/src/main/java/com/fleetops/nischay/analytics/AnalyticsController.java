package com.fleetops.nischay.analytics;

import com.fleetops.nischay.dto.response.DeliveryStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/deliveries")
    @PreAuthorize("hasAuthority('analytics:view')")
    public DeliveryStatsResponse getStats() {
        return analyticsService.getDeliveryStats();
    }
}