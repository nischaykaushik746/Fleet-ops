package com.fleetops.nischay.admin;

import com.fleetops.nischay.driver.Driver;
import com.fleetops.nischay.driver.DriverStatus;
import com.fleetops.nischay.repository.DriverRepository;
import com.fleetops.nischay.repository.UserRepository;
import com.fleetops.nischay.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    @PreAuthorize("hasAuthority('driver:manage')")
    public Driver createDriver(Long userId, String name) {

        User user = userRepository.findById(userId).orElseThrow();

        Driver driver = Driver.builder()
                .user(user)
                .name(name)
                .status(DriverStatus.AVAILABLE)
                .build();

        return driverRepository.save(driver);
    }

    @PreAuthorize("hasAuthority('driver:manage')")
    public Driver updateStatus(Long driverId, DriverStatus status) {

        Driver driver = driverRepository.findById(driverId).orElseThrow();
        driver.setStatus(status);

        return driverRepository.save(driver);
    }
}