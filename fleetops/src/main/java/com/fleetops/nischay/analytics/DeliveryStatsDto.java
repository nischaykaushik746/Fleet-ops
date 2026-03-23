package com.fleetops.nischay.analytics;

import lombok.Data;

@Data
public class DeliveryStatsDto {
    private long totalTrips;
    private long completedTrips;
    private long cancelledTrips;
}