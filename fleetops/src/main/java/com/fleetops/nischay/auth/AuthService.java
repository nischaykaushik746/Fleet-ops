package com.fleetops.nischay.auth;

import com.fleetops.nischay.aop.TrackExecution;
import com.fleetops.nischay.dto.response.AuthResponse;
import com.fleetops.nischay.exception.BusinessRuleException;
import com.fleetops.nischay.exception.DuplicateResourceException;
import com.fleetops.nischay.repository.RefreshTokenRepository;
import com.fleetops.nischay.repository.UserRepository;
import com.fleetops.nischay.role.RoleType;
import com.fleetops.nischay.security.JwtUtil;
import com.fleetops.nischay.security.RefreshToken;
import com.fleetops.nischay.security.TokenBlacklistService;
import com.fleetops.nischay.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    @TrackExecution
    @Transactional
    public AuthResponse signup(String username, String password) {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new DuplicateResourceException("Username '" + username + "' already exists");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(Set.of(RoleType.DRIVER))
                .build();

        userRepository.save(user);

        log.info("User created: {}", username);

        return AuthResponse.builder()
                .message("User created successfully")
                .build();
    }

    @TrackExecution
    @Transactional
    public AuthResponse login(User user) {
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

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenExpiration() / 1000)
                .build();
    }

    @TrackExecution
    @Transactional
    public AuthResponse refreshToken(String token) {

        // Validate it's a refresh token
        if (!jwtUtil.isRefreshToken(token)) {
            throw new BusinessRuleException("Invalid token type — expected refresh token");
        }

        if (jwtUtil.isTokenExpired(token)) {
            throw new BusinessRuleException("Refresh token expired");
        }

        // Find in DB
        RefreshToken stored = refreshTokenRepository.findByTokenAndRevokedFalse(token)
                .orElseThrow(() -> new BusinessRuleException("Refresh token not found or revoked"));

        // Revoke old refresh token (rotation)
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        // Issue new tokens
        String username = jwtUtil.extractUsername(token);
        Long userId = jwtUtil.extractUserId(token);

        String newAccess = jwtUtil.generateAccessToken(username, userId);
        String newRefresh = jwtUtil.generateRefreshToken(username, userId);

        RefreshToken newRt = RefreshToken.builder()
                .token(newRefresh)
                .userId(userId)
                .username(username)
                .expiresAt(Instant.now().plusMillis(jwtUtil.getAccessTokenExpiration() * 14))
                .revoked(false)
                .createdAt(Instant.now())
                .build();
        refreshTokenRepository.save(newRt);

        log.info("Token refreshed for user: {}", username);

        return AuthResponse.builder()
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenExpiration() / 1000)
                .build();
    }

    @Transactional
    public void logout(String accessToken) {
        String tokenId = jwtUtil.extractTokenId(accessToken);
        tokenBlacklistService.blacklist(tokenId, jwtUtil.extractExpiration(accessToken));

        Long userId = jwtUtil.extractUserId(accessToken);
        refreshTokenRepository.revokeAllByUserId(userId);

        log.info("User logged out, all tokens revoked: userId={}", userId);
    }
}