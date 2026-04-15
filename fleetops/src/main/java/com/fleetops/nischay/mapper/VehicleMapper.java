package com.fleetops.nischay.mapper;

import com.fleetops.nischay.dto.response.VehicleResponse;
import com.fleetops.nischay.fleet.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(target = "status", expression = "java(vehicle.getStatus().name())")
    VehicleResponse toResponse(Vehicle vehicle);
}