package com.fleetops.nischay.role;

public enum PermissionType {

    TRIP_CREATE("trip:create"),
    TRIP_VIEW("trip:view"),
    TRIP_ASSIGN("trip:assign"),

    VEHICLE_MANAGE("vehicle:manage"),
    DRIVER_MANAGE("driver:manage"),

    ANALYTICS_VIEW("analytics:view"),

    USER_MANAGE("user:manage");

    private final String permission;

    PermissionType(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}