package com.fleetops.nischay.repository;

import com.fleetops.nischay.driver.Driver;
import com.fleetops.nischay.driver.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    List<Driver> findByStatus(DriverStatus status);

    Optional<Driver> findByUserId(Long userId);

    long countByStatus(DriverStatus status);
}