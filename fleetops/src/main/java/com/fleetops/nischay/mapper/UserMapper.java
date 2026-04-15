package com.fleetops.nischay.mapper;

import com.fleetops.nischay.dto.response.UserResponse;
import com.fleetops.nischay.role.RoleType;
import com.fleetops.nischay.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStrings")
    UserResponse toResponse(User user);

    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<RoleType> roles) {
        if (roles == null) return Set.of();
        return roles.stream().map(RoleType::name).collect(Collectors.toSet());
    }
}