package com.fleetops.nischay.security;

import com.fleetops.nischay.repository.RefreshTokenRepository;
import com.fleetops.nischay.repository.UserRepository;
import com.fleetops.nischay.role.RoleType;
import com.fleetops.nischay.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByUsername(email)
                .orElseGet(() -> {
                    log.info("Creating new user from OAuth2: {}", email);
                    return userRepository.save(
                            User.builder()
                                    .username(email)
                                    .email(email)
                                    .roles(Set.of(RoleType.DRIVER))
                                    .build()
                    );
                });

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getId());

        // Persist refresh token
        RefreshToken rt = RefreshToken.builder()
                .token(refreshToken)
                .userId(user.getId())
                .username(user.getUsername())
                .expiresAt(Instant.now().plusMillis(jwtUtil.getAccessTokenExpiration() * 14))
                .revoked(false)
                .createdAt(Instant.now())
                .build();
        refreshTokenRepository.save(rt);

        response.setContentType("application/json");
        response.getWriter().write(
                "{\"accessToken\":\"" + accessToken
                        + "\",\"refreshToken\":\"" + refreshToken
                        + "\",\"tokenType\":\"Bearer\"}"
        );
    }
}