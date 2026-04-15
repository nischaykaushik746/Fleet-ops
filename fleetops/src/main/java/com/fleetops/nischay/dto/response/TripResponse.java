package com.fleetops.nischay.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TripResponse {
    private Long id;
    private String source;
    private String destination;
    private String status;
    private Instant startTime;
    private Instant endTime;
    private Long driverId;
    private String driverName;
    private Long vehicleId;
    private String vehicleNumber;
    private Instant createdAt;
}