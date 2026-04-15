package com.fleetops.nischay.admin;

import com.fleetops.nischay.exception.DuplicateResourceException;
import com.fleetops.nischay.exception.ResourceNotFoundException;
import com.fleetops.nischay.fleet.Vehicle;
import com.fleetops.nischay.fleet.VehicleStatus;
import com.fleetops.nischay.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    @PreAuthorize("hasAuthority('vehicle:manage')")
    @Transactional
    public Vehicle registerVehicle(String number, String type, int capacity) {

        if (vehicleRepository.findByVehicleNumber(number).isPresent()) {
            throw new DuplicateResourceException("Vehicle with number '" + number + "' already exists");
        }

        Vehicle vehicle = Vehicle.builder()
                .vehicleNumber(number)
                .type(type)
                .capacity(capacity)
                .status(VehicleStatus.ACTIVE)
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle registered: id={} number={}", saved.getId(), number);
        return saved;
    }

    @PreAuthorize("hasAuthority('vehicle:manage')")
    @Transactional
    public Vehicle updateStatus(Long id, VehicleStatus status) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));

        VehicleStatus oldStatus = vehicle.getStatus();
        vehicle.setStatus(status);

        log.info("Vehicle status updated: id={} {} -> {}", id, oldStatus, status);
        return vehicleRepository.save(vehicle);
    }
}