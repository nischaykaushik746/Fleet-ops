package com.fleetops.nischay.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void generateAndValidateAccessToken() {
        String token = jwtUtil.generateAccessToken("testuser", 1L);

        assertThat(token).isNotBlank();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("testuser");
        assertThat(jwtUtil.extractUserId(token)).isEqualTo(1L);
        assertThat(jwtUtil.isAccessToken(token)).isTrue();
        assertThat(jwtUtil.isRefreshToken(token)).isFalse();
        assertThat(jwtUtil.isTokenExpired(token)).isFalse();
        assertThat(jwtUtil.validateToken(token, "testuser")).isTrue();
        assertThat(jwtUtil.validateToken(token, "wronguser")).isFalse();
    }

    @Test
    void generateAndValidateRefreshToken() {
        String token = jwtUtil.generateRefreshToken("testuser", 1L);

        assertThat(token).isNotBlank();
        assertThat(jwtUtil.isRefreshToken(token)).isTrue();
        assertThat(jwtUtil.isAccessToken(token)).isFalse();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("testuser");
    }

    @Test
    void tokenId_shouldBeUnique() {
        String t1 = jwtUtil.generateAccessToken("user", 1L);
        String t2 = jwtUtil.generateAccessToken("user", 1L);

        assertThat(jwtUtil.extractTokenId(t1)).isNotEqualTo(jwtUtil.extractTokenId(t2));
    }
}