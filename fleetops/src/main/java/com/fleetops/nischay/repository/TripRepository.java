package com.fleetops.nischay.repository;

import com.fleetops.nischay.trip.Trip;
import com.fleetops.nischay.trip.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByStatus(TripStatus status);

    @Query("SELECT t FROM Trip t LEFT JOIN FETCH t.driver LEFT JOIN FETCH t.vehicle WHERE t.driver.id = :driverId ORDER BY t.createdAt DESC")
    List<Trip> findByDriverId(Long driverId);

    long countByStatus(TripStatus status);

    @Query("SELECT t FROM Trip t LEFT JOIN FETCH t.driver LEFT JOIN FETCH t.vehicle WHERE t.status IN :statuses")
    List<Trip> findByStatusIn(List<TripStatus> statuses);
}