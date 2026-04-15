package com.fleetops.nischay.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryStatsResponse {
    private long totalTrips;
    private long completedTrips;
    private long cancelledTrips;
    private double completionRate;
    private double cancellationRate;
}