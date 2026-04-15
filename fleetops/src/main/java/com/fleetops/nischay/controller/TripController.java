package com.fleetops.nischay.controller;

import com.fleetops.nischay.dto.request.CreateTripRequest;
import com.fleetops.nischay.dto.response.TripResponse;
import com.fleetops.nischay.idempotency.IdempotencyService;
import com.fleetops.nischay.mapper.TripMapper;
import com.fleetops.nischay.service.TripService;
import com.fleetops.nischay.trip.Trip;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final IdempotencyService idempotencyService;
    private final TripMapper tripMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripResponse createTrip(
            @RequestHeader("Idempotency-Key") String key,
            @Valid @RequestBody CreateTripRequest request) {

        // Check idempotency
        TripResponse cached = idempotencyService.get(key, TripResponse.class);
        if (cached != null) {
            return cached;
        }

        Trip trip = tripService.createTrip(request.getSource(), request.getDestination());
        TripResponse response = tripMapper.toResponse(trip);

        idempotencyService.save(key, response);

        return response;
    }

    @GetMapping("/driver/{driverId}")
    public List<TripResponse> getTrips(@PathVariable Long driverId) {
        return tripService.getTripsForDriver(driverId).stream()
                .map(tripMapper::toResponse)
                .toList();
    }

    @PutMapping("/{tripId}/complete")
    public TripResponse completeTrip(@PathVariable Long tripId) {
        Trip trip = tripService.completeTrip(tripId);
        return tripMapper.toResponse(trip);
    }

    @PutMapping("/{tripId}/cancel")
    public TripResponse cancelTrip(@PathVariable Long tripId,
                                   @RequestParam(required = false) String reason) {
        Trip trip = tripService.cancelTrip(tripId, reason);
        return tripMapper.toResponse(trip);
    }
}