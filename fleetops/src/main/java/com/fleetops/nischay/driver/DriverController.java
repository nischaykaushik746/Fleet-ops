package com.fleetops.nischay.driver;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('DRIVER')")
    public String updateStatus(@PathVariable Long id) {
        return "Driver status updated";
    }
}