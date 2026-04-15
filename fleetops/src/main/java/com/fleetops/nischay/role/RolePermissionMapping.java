package com.fleetops.nischay.role;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fleetops.nischay.role.PermissionType.*;
import static com.fleetops.nischay.role.RoleType.*;

public final class RolePermissionMapping {

    private RolePermissionMapping() {}

    private static final Map<RoleType, Set<PermissionType>> MAPPING = Map.of(
            DRIVER, Set.of(TRIP_VIEW),
            OPS_MANAGER, Set.of(TRIP_CREATE, TRIP_ASSIGN, TRIP_VIEW, VEHICLE_MANAGE, DRIVER_MANAGE, ANALYTICS_VIEW),
            ADMIN, Set.of(TRIP_CREATE, TRIP_ASSIGN, TRIP_VIEW, VEHICLE_MANAGE, DRIVER_MANAGE, ANALYTICS_VIEW, USER_MANAGE)
    );

    public static Set<SimpleGrantedAuthority> getAuthoritiesForRole(RoleType role) {
        return MAPPING.getOrDefault(role, Collections.emptySet())
                .stream()
                .map(p -> new SimpleGrantedAuthority(p.getPermission()))
                .collect(Collectors.toUnmodifiableSet());
    }
}