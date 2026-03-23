package com.fleetops.nischay.admin;

import com.fleetops.nischay.fleet.Vehicle;
import com.fleetops.nischay.fleet.VehicleStatus;
import com.fleetops.nischay.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    @PreAuthorize("hasAuthority('vehicle:manage')")
    public Vehicle registerVehicle(String number, String type, int capacity) {

        Vehicle vehicle = Vehicle.builder()
                .vehicleNumber(number)
                .type(type)
                .capacity(capacity)
                .status(VehicleStatus.ACTIVE)
                .build();

        return vehicleRepository.save(vehicle);
    }

    @PreAuthorize("hasAuthority('vehicle:manage')")
    public Vehicle updateStatus(Long id, VehicleStatus status) {

        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow();
        vehicle.setStatus(status);

        return vehicleRepository.save(vehicle);
    }
}