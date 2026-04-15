package com.fleetops.nischay.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class DriverResponse {
    private Long id;
    private String name;
    private String licenseNumber;
    private String phone;
    private String status;
    private Long userId;
    private Long assignedVehicleId;
    private Instant createdAt;
}