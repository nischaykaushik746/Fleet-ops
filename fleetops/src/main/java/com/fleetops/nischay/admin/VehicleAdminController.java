package com.fleetops.nischay.admin;

import com.fleetops.nischay.dto.request.RegisterVehicleRequest;
import com.fleetops.nischay.dto.response.VehicleResponse;
import com.fleetops.nischay.fleet.VehicleStatus;
import com.fleetops.nischay.mapper.VehicleMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/vehicles")
@RequiredArgsConstructor
public class VehicleAdminController {

    private final VehicleService vehicleService;
    private final VehicleMapper vehicleMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse register(@Valid @RequestBody RegisterVehicleRequest request) {
        return vehicleMapper.toResponse(
                vehicleService.registerVehicle(request.getNumber(), request.getType(), request.getCapacity()));
    }

    @PutMapping("/{id}/status")
    public VehicleResponse updateStatus(@PathVariable Long id,
                                        @RequestParam VehicleStatus status) {
        return vehicleMapper.toResponse(vehicleService.updateStatus(id, status));
    }
}