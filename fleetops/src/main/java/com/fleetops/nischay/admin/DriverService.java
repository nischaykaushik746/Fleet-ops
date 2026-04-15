package com.fleetops.nischay.admin;

import com.fleetops.nischay.driver.Driver;
import com.fleetops.nischay.driver.DriverStatus;
import com.fleetops.nischay.exception.DuplicateResourceException;
import com.fleetops.nischay.exception.ResourceNotFoundException;
import com.fleetops.nischay.repository.DriverRepository;
import com.fleetops.nischay.repository.UserRepository;
import com.fleetops.nischay.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    @PreAuthorize("hasAuthority('driver:manage')")
    @Transactional
    public Driver createDriver(Long userId, String name, String licenseNumber, String phone) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (driverRepository.findByUserId(userId).isPresent()) {
            throw new DuplicateResourceException("Driver already exists for userId: " + userId);
        }

        Driver driver = Driver.builder()
                .user(user)
                .name(name)
                .licenseNumber(licenseNumber)
                .phone(phone)
                .status(DriverStatus.AVAILABLE)
                .build();

        Driver saved = driverRepository.save(driver);
        log.info("Driver created: id={} userId={} name={}", saved.getId(), userId, name);
        return saved;
    }

    @PreAuthorize("hasAuthority('driver:manage')")
    @Transactional
    public Driver updateStatus(Long driverId, DriverStatus status) {

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", driverId));

        DriverStatus oldStatus = driver.getStatus();
        driver.setStatus(status);

        log.info("Driver status updated: id={} {} -> {}", driverId, oldStatus, status);
        return driverRepository.save(driver);
    }
}