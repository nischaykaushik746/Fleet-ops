package com.fleetops.nischay.controller;

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

    @PostMapping
    public Trip createTrip(@RequestParam String source,
                           @RequestParam String destination) {

        return tripService.createTrip(source, destination);
    }
    @GetMapping("/driver/{driverId}")
    public List<Trip> getTrips(@PathVariable Long driverId) {
        return tripService.getTripsForDriver(driverId);
    }
}