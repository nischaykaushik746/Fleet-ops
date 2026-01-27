package com.fleetops.nischay.security;

import com.fleetops.enums.PermissionType;
import com.fleetops.enums.RoleType;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class RolePermissionMapping {

    private static final Map<RoleType, Set<PermissionType>> ROLE_PERMISSIONS =
            new EnumMap<>(RoleType.class);

    static {
        ROLE_PERMISSIONS.put(
                RoleType.ADMIN,
                EnumSet.allOf(PermissionType.class)
        );

        ROLE_PERMISSIONS.put(
                RoleType.OPS_MANAGER,
                EnumSet.of(
                        PermissionType.CREATE_DELIVERY,
                        PermissionType.ASSIGN_DELIVERY,
                        PermissionType.VIEW_ANALYTICS
                )
        );

        ROLE_PERMISSIONS.put(
                RoleType.DRIVER,
                EnumSet.of(
                        PermissionType.UPDATE_DELIVERY_STATUS
                )
        );

        ROLE_PERMISSIONS.put(
                RoleType.CUSTOMER,
                EnumSet.of(
                        PermissionType.CREATE_DELIVERY
                )
        );

        ROLE_PERMISSIONS.put(
                RoleType.SUPPORT,
                EnumSet.of(
                        PermissionType.VIEW_ANALYTICS
                )
        );
    }

    private RolePermissionMapping() {}

    public static Set<PermissionType> getPermissions(RoleType roleType) {
        return ROLE_PERMISSIONS.getOrDefault(roleType, EnumSet.noneOf(PermissionType.class));
    }
}