package com.fleetops.nischay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTripRequest {

    @NotBlank(message = "Source is required")
    @Size(max = 255)
    private String source;

    @NotBlank(message = "Destination is required")
    @Size(max = 255)
    private String destination;
}