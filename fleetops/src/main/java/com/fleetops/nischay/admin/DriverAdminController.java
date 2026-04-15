package com.fleetops.nischay.admin;

import com.fleetops.nischay.driver.DriverStatus;
import com.fleetops.nischay.dto.request.CreateDriverRequest;
import com.fleetops.nischay.dto.response.DriverResponse;
import com.fleetops.nischay.mapper.DriverMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/drivers")
@RequiredArgsConstructor
public class DriverAdminController {

    private final DriverService driverService;
    private final DriverMapper driverMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverResponse createDriver(@Valid @RequestBody CreateDriverRequest request) {
        return driverMapper.toResponse(
                driverService.createDriver(request.getUserId(), request.getName(),
                        request.getLicenseNumber(), request.getPhone()));
    }

    @PutMapping("/{id}/status")
    public DriverResponse updateStatus(@PathVariable Long id,
                                       @RequestParam DriverStatus status) {
        return driverMapper.toResponse(driverService.updateStatus(id, status));
    }
}