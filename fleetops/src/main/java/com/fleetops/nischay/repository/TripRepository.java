package com.fleetops.nischay.repository;

import com.fleetops.nischay.trip.Trip;
import com.fleetops.nischay.trip.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByStatus(TripStatus status);
}