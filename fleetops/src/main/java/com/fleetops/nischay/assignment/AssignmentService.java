package com.fleetops.nischay.assignment;

import com.fleetops.nischay.driver.Driver;
import com.fleetops.nischay.driver.DriverStatus;
import com.fleetops.nischay.fleet.Vehicle;
import com.fleetops.nischay.fleet.VehicleStatus;
import com.fleetops.nischay.repository.DriverRepository;
import com.fleetops.nischay.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    public Driver getAvailableDriver() {
        List<Driver> drivers = driverRepository.findByStatus(DriverStatus.AVAILABLE);
        if (drivers.isEmpty()) throw new RuntimeException("No drivers available");
        return drivers.get(0);
    }

    public Vehicle getAvailableVehicle() {
        List<Vehicle> vehicles = vehicleRepository.findByStatus(VehicleStatus.ACTIVE);
        if (vehicles.isEmpty()) throw new RuntimeException("No vehicles available");
        return vehicles.get(0);
    }
}