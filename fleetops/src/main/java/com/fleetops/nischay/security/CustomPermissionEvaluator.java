package com.fleetops.nischay.security;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication auth, Object target, Object permission) {
        if (auth == null || permission == null) return false;
        String perm = permission.toString();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(perm));
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId,
                                 String targetType, Object permission) {
        return hasPermission(auth, null, permission);
    }
}