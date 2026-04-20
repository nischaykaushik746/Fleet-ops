package com.fleetops.nischay.controller;

import com.fleetops.nischay.BaseIntegrationTest;
import com.fleetops.nischay.dto.request.CreateTripRequest;
import com.fleetops.nischay.driver.Driver;
import com.fleetops.nischay.driver.DriverStatus;
import com.fleetops.nischay.fleet.Vehicle;
import com.fleetops.nischay.fleet.VehicleStatus;
import com.fleetops.nischay.repository.DriverRepository;
import com.fleetops.nischay.repository.TripRepository;
import com.fleetops.nischay.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TripControllerTest extends BaseIntegrationTest {

    @Autowired private TripRepository tripRepository;
    @Autowired private DriverRepository driverRepository;
    @Autowired private VehicleRepository vehicleRepository;

    @BeforeEach
    void setupTrips() {
        tripRepository.deleteAll();
        driverRepository.deleteAll();
        vehicleRepository.deleteAll();

        // Create available driver and vehicle
        driverRepository.save(Driver.builder()
                .user(driverUser)
                .name("Test Driver")
                .status(DriverStatus.AVAILABLE)
                .build());

        vehicleRepository.save(Vehicle.builder()
                .vehicleNumber("KA-01-1234")
                .type("Truck")
                .capacity(10)
                .status(VehicleStatus.ACTIVE)
                .build());
    }

    @Test
    void createTrip_asOpsManager_shouldSucceed() throws Exception {
        CreateTripRequest request = new CreateTripRequest();
        request.setSource("Bangalore");
        request.setDestination("Chennai");

        mockMvc.perform(post("/trips")
                        .header("Authorization", "Bearer " + opsToken)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.source").value("Bangalore"))
                .andExpect(jsonPath("$.destination").value("Chennai"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void createTrip_asDriver_shouldBeForbidden() throws Exception {
        CreateTripRequest request = new CreateTripRequest();
        request.setSource("Bangalore");
        request.setDestination("Chennai");

        mockMvc.perform(post("/trips")
                        .header("Authorization", "Bearer " + driverToken)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createTrip_idempotency_shouldReturnSameResponse() throws Exception {
        CreateTripRequest request = new CreateTripRequest();
        request.setSource("Mumbai");
        request.setDestination("Pune");

        String idempotencyKey = UUID.randomUUID().toString();

        // First call
        mockMvc.perform(post("/trips")
                        .header("Authorization", "Bearer " + adminToken)
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second call — same key
        mockMvc.perform(post("/trips")
                        .header("Authorization", "Bearer " + adminToken)
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.source").value("Mumbai"));
    }

    @Test
    void createTrip_noAuth_shouldBeUnauthorized() throws Exception {
        CreateTripRequest request = new CreateTripRequest();
        request.setSource("Delhi");
        request.setDestination("Agra");

        mockMvc.perform(post("/trips")
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getTripsForDriver_asAdmin_shouldSucceed() throws Exception {
        mockMvc.perform(get("/trips/driver/" + driverUser.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}