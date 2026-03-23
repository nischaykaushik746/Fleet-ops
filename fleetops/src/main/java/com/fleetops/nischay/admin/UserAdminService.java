package com.fleetops.nischay.admin;

import com.fleetops.nischay.repository.UserRepository;
import com.fleetops.nischay.role.RoleType;
import com.fleetops.nischay.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;

    @PreAuthorize("hasAuthority('user:manage')")
    public User assignRole(Long userId, RoleType role) {

        User user = userRepository.findById(userId).orElseThrow();
        user.getRoles().add(role);

        return userRepository.save(user);
    }
}