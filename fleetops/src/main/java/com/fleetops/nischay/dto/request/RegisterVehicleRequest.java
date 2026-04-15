package com.fleetops.nischay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RegisterVehicleRequest {

    @NotBlank(message = "Vehicle number is required")
    private String number;

    @NotBlank(message = "Vehicle type is required")
    private String type;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    private Integer capacity;
}