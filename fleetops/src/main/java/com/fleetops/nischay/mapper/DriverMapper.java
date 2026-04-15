package com.fleetops.nischay.mapper;

import com.fleetops.nischay.driver.Driver;
import com.fleetops.nischay.dto.response.DriverResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "assignedVehicleId", source = "assignedVehicle.id")
    @Mapping(target = "status", expression = "java(driver.getStatus().name())")
    DriverResponse toResponse(Driver driver);
}