package com.fleetops.nischay.assignment;

import com.fleetops.nischay.driver.Driver;
import com.fleetops.nischay.driver.DriverStatus;
import com.fleetops.nischay.exception.BusinessRuleException;
import com.fleetops.nischay.fleet.Vehicle;
import com.fleetops.nischay.fleet.VehicleStatus;
import com.fleetops.nischay.repository.DriverRepository;
import com.fleetops.nischay.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    @Mock private DriverRepository driverRepository;
    @Mock private VehicleRepository vehicleRepository;

    @InjectMocks private AssignmentService assignmentService;

    @Test
    void getAvailableDriver_shouldReturnDriver() {
        Driver d = Driver.builder().id(1L).name("John").status(DriverStatus.AVAILABLE).build();
        when(driverRepository.findByStatus(DriverStatus.AVAILABLE)).thenReturn(List.of(d));

        Driver result = assignmentService.getAvailableDriver();
        assertThat(result.getName()).isEqualTo("John");
    }

    @Test
    void getAvailableDriver_noneAvailable_shouldThrow() {
        when(driverRepository.findByStatus(DriverStatus.AVAILABLE)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> assignmentService.getAvailableDriver())
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("No drivers available");
    }

    @Test
    void getAvailableVehicle_shouldReturnVehicle() {
        Vehicle v = Vehicle.builder().id(1L).vehicleNumber("KA-01").status(VehicleStatus.ACTIVE).build();
        when(vehicleRepository.findByStatus(VehicleStatus.ACTIVE)).thenReturn(List.of(v));

        Vehicle result = assignmentService.getAvailableVehicle();
        assertThat(result.getVehicleNumber()).isEqualTo("KA-01");
    }

    @Test
    void getAvailableVehicle_noneAvailable_shouldThrow() {
        when(vehicleRepository.findByStatus(VehicleStatus.ACTIVE)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> assignmentService.getAvailableVehicle())
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("No vehicles available");
    }
}