package com.fleetops.nischay.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeliveryStatsDto {
    private long totalDeliveries;
    private long completedDeliveries;
    private long failedDeliveries;
}
