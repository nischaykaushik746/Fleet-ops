package com.fleetops.nischay.repository;

import com.fleetops.nischay.fleet.Vehicle;
import com.fleetops.nischay.fleet.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByStatus(VehicleStatus status);

    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);

    long countByStatus(VehicleStatus status);
}