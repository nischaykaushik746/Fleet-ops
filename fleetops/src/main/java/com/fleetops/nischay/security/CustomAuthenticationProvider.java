package com.fleetops.nischay.security;

import com.fleetops.nischay.audit.AuditService;
import com.fleetops.nischay.repository.UserRepository;
import com.fleetops.nischay.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final AuditService auditService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Check brute-force lock
        if (loginAttemptService.isBlocked(username)) {
            log.error("BLOCKED login attempt: user={}", username);
            auditService.logAction(username, "LOGIN_BLOCKED", "Account temporarily locked");
            throw new LockedException("Account temporarily locked due to too many failed attempts");
        }

        // Find user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    loginAttemptService.loginFailed(username);
                    return new BadCredentialsException("Invalid credentials");
                });

        // Check account status
        if (!user.isEnabled()) {
            throw new DisabledException("Account is disabled");
        }
        if (!user.isAccountNonLocked()) {
            throw new LockedException("Account is locked");
        }

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            loginAttemptService.loginFailed(username);
            int remaining = loginAttemptService.getRemainingAttempts(username);
            log.warn("Failed login: user={}, remaining_attempts={}", username, remaining);
            auditService.logAction(username, "LOGIN_FAILED", "Bad credentials");
            throw new BadCredentialsException("Invalid credentials");
        }

        // Success
        loginAttemptService.loginSuccess(username);
        auditService.logAction(username, "LOGIN_SUCCESS", "Authenticated successfully");
        log.info("Successful login: user={}", username);

        return new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}