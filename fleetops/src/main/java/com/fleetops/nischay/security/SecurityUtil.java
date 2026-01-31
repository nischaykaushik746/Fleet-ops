package com.fleetops.nischay.security;

import com.fleetops.nischay.enums.RoleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    private SecurityUtil() {}

    public static boolean hasRole(RoleType role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        return auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(role.name()));
    }
}
