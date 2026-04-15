package com.fleetops.nischay.mapper;

import com.fleetops.nischay.dto.response.TripResponse;
import com.fleetops.nischay.trip.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TripMapper {

    @Mapping(target = "status", expression = "java(trip.getStatus().name())")
    @Mapping(target = "driverId", source = "driver.id")
    @Mapping(target = "driverName", source = "driver.name")
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "vehicleNumber", source = "vehicle.vehicleNumber")
    TripResponse toResponse(Trip trip);
}