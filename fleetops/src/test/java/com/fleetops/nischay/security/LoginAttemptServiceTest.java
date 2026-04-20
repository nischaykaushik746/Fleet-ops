package com.fleetops.nischay.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginAttemptServiceTest {

    private LoginAttemptService service;

    @BeforeEach
    void setUp() {
        service = new LoginAttemptService();
    }

    @Test
    void shouldNotBlockOnFirstAttempt() {
        assertThat(service.isBlocked("user1")).isFalse();
    }

    @Test
    void shouldBlockAfterMaxAttempts() {
        for (int i = 0; i < 5; i++) {
            service.loginFailed("user1");
        }
        assertThat(service.isBlocked("user1")).isTrue();
    }

    @Test
    void shouldResetOnSuccess() {
        for (int i = 0; i < 4; i++) {
            service.loginFailed("user1");
        }
        service.loginSuccess("user1");
        assertThat(service.isBlocked("user1")).isFalse();
    }

    @Test
    void remainingAttempts_shouldDecrease() {
        assertThat(service.getRemainingAttempts("user1")).isEqualTo(5);
        service.loginFailed("user1");
        assertThat(service.getRemainingAttempts("user1")).isEqualTo(4);
    }
}