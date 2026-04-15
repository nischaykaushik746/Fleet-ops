package com.fleetops.nischay.service;

import com.fleetops.nischay.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("tripSecurityService")
public class TripSecurityService {

    public boolean isDriverOwner(Long driverId, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) return false;

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user.getId().equals(driverId);
        }
        return false;
    }
}