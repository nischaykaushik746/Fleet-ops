package com.fleetops.nischay.security;

import com.fleetops.nischay.repository.UserRepository;
import com.fleetops.nischay.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    @Override
    public Authentication authenticate(Authentication authentication) {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (loginAttemptService.isBlocked(username)) {
            log.error("BLOCKED USER ATTEMPT: {}", username);
            throw new RuntimeException("Account locked");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authentication failed"));

        if (!passwordEncoder.matches(password, user.getPassword())) {

            log.warn("FAILED LOGIN: {}", username);

            loginAttemptService.loginFailed(username);
            throw new RuntimeException("Authentication failed");
        }

        loginAttemptService.loginSuccess(username);

        log.info("SUCCESS LOGIN: {}", username);

        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}