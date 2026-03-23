package com.fleetops.nischay.admin;

import com.fleetops.nischay.driver.Driver;
import com.fleetops.nischay.driver.DriverStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/drivers")
@RequiredArgsConstructor
public class DriverAdminController {

    private final DriverService driverService;

    @PostMapping
    public Driver createDriver(@RequestParam Long userId,
                               @RequestParam String name) {
        return driverService.createDriver(userId, name);
    }

    @PutMapping("/{id}/status")
    public Driver updateStatus(@PathVariable Long id,
                               @RequestParam DriverStatus status) {
        return driverService.updateStatus(id, status);
    }
}