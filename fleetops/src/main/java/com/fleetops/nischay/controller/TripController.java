package com.fleetops.nischay.controller;

import com.fleetops.nischay.idempotency.IdempotencyService;
import com.fleetops.nischay.service.TripService;
import com.fleetops.nischay.trip.Trip;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final IdempotencyService idempotencyService;

    @PostMapping
    public Object createTrip(@RequestHeader("Idempotency-Key") String key,
                             @RequestParam String source,
                             @RequestParam String destination) {

        if (idempotencyService.isDuplicate(key)) {
            return idempotencyService.get(key);
        }

        Trip trip = tripService.createTrip(source, destination);

        idempotencyService.save(key, trip);

        return trip;
    }

    @GetMapping("/driver/{driverId}")
    public List<Trip> getTrips(@PathVariable Long driverId) {
        return tripService.getTripsForDriver(driverId);
    }
}