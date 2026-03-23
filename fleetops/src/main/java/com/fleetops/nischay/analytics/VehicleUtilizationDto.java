package com.fleetops.nischay.analytics;

import lombok.Data;

@Data
public class VehicleUtilizationDto {
    private Long vehicleId;
    private long totalTrips;
}