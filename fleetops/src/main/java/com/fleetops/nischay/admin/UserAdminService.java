package com.fleetops.nischay.admin;

import com.fleetops.nischay.exception.BusinessRuleException;
import com.fleetops.nischay.exception.ResourceNotFoundException;
import com.fleetops.nischay.repository.UserRepository;
import com.fleetops.nischay.role.RoleType;
import com.fleetops.nischay.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;

    @PreAuthorize("hasAuthority('user:manage')")
    @Transactional
    public User assignRole(Long userId, RoleType role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (user.getRoles().contains(role)) {
            throw new BusinessRuleException("User already has role: " + role);
        }

        user.getRoles().add(role);
        log.info("Role {} assigned to userId={}", role, userId);
        return userRepository.save(user);
    }

    @PreAuthorize("hasAuthority('user:manage')")
    @Transactional
    public User removeRole(Long userId, RoleType role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (!user.getRoles().contains(role)) {
            throw new BusinessRuleException("User doesn't have role: " + role);
        }

        if (user.getRoles().size() <= 1) {
            throw new BusinessRuleException("Cannot remove last role from user");
        }

        user.getRoles().remove(role);
        log.info("Role {} removed from userId={}", role, userId);
        return userRepository.save(user);
    }

    @PreAuthorize("hasAuthority('user:manage')")
    @Transactional
    public User disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setEnabled(false);
        log.info("User disabled: userId={}", userId);
        return userRepository.save(user);
    }

    @PreAuthorize("hasAuthority('user:manage')")
    @Transactional
    public User enableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setEnabled(true);
        log.info("User enabled: userId={}", userId);
        return userRepository.save(user);
    }
}