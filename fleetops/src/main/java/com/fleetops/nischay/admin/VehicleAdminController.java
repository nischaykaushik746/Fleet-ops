package com.fleetops.nischay.admin;

import com.fleetops.nischay.fleet.Vehicle;
import com.fleetops.nischay.fleet.VehicleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/vehicles")
@RequiredArgsConstructor
public class VehicleAdminController {

    private final VehicleService vehicleService;

    @PostMapping
    public Vehicle register(@RequestParam String number,
                            @RequestParam String type,
                            @RequestParam int capacity) {
        return vehicleService.registerVehicle(number, type, capacity);
    }

    @PutMapping("/{id}/status")
    public Vehicle updateStatus(@PathVariable Long id,
                                @RequestParam VehicleStatus status) {
        return vehicleService.updateStatus(id, status);
    }
}