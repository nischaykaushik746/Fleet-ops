package com.fleetops.nischay.assignment;

import com.fleetops.nischay.driver.Driver;
import com.fleetops.nischay.driver.DriverStatus;
import com.fleetops.nischay.exception.BusinessRuleException;
import com.fleetops.nischay.fleet.Vehicle;
import com.fleetops.nischay.fleet.VehicleStatus;
import com.fleetops.nischay.repository.DriverRepository;
import com.fleetops.nischay.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    @Cacheable(value = "availableDrivers", unless = "#result.isEmpty()")
    public List<Driver> getAvailableDrivers() {
        log.debug("Fetching available drivers from DB (cache miss)");
        return driverRepository.findByStatus(DriverStatus.AVAILABLE);
    }

    public Driver getAvailableDriver() {
        List<Driver> drivers = getAvailableDrivers();
        if (drivers.isEmpty()) {
            throw new BusinessRuleException("No drivers available for assignment");
        }
        // Round-robin could be added here for fairness
        return drivers.get(0);
    }

    public Vehicle getAvailableVehicle() {
        List<Vehicle> vehicles = vehicleRepository.findByStatus(VehicleStatus.ACTIVE);
        if (vehicles.isEmpty()) {
            throw new BusinessRuleException("No vehicles available for assignment");
        }
        return vehicles.get(0);
    }

    @CacheEvict(value = "availableDrivers", allEntries = true)
    public void invalidateDriverCache() {
        log.debug("Driver cache invalidated");
    }
}