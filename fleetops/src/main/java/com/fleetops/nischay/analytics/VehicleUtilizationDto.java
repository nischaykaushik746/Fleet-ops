package com.fleetops.nischay.analytics;

public class VehicleUtilizationDto {

    private final String vehicleType;
    private final long count;

    public VehicleUtilizationDto(String vehicleType, long count) {
        this.vehicleType = vehicleType;
        this.count = count;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public long getCount() {
        return count;
    }
}
