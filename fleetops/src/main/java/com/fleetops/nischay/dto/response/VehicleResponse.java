package com.fleetops.nischay.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class VehicleResponse {
    private Long id;
    private String vehicleNumber;
    private String type;
    private int capacity;
    private String status;
    private Instant createdAt;
}