package com.fleetops.nischay.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION_SECONDS = 900; // 15 min

    private final Map<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    public void loginFailed(String username) {
        AttemptInfo info = attempts.compute(username, (key, existing) -> {
            if (existing == null || existing.isExpired()) {
                return new AttemptInfo(1, Instant.now().plusSeconds(LOCK_DURATION_SECONDS));
            }
            return new AttemptInfo(existing.count + 1, existing.lockUntil);
        });
        log.warn("Failed login for '{}': attempt #{}", username, info.count);
    }

    public void loginSuccess(String username) {
        attempts.remove(username);
    }

    public boolean isBlocked(String username) {
        AttemptInfo info = attempts.get(username);
        if (info == null) return false;

        if (info.isExpired()) {
            attempts.remove(username);
            return false;
        }

        return info.count >= MAX_ATTEMPTS;
    }

    public int getRemainingAttempts(String username) {
        AttemptInfo info = attempts.get(username);
        if (info == null || info.isExpired()) return MAX_ATTEMPTS;
        return Math.max(0, MAX_ATTEMPTS - info.count);
    }

    private static class AttemptInfo {
        final int count;
        final Instant lockUntil;

        AttemptInfo(int count, Instant lockUntil) {
            this.count = count;
            this.lockUntil = lockUntil;
        }

        boolean isExpired() {
            return Instant.now().isAfter(lockUntil);
        }
    }
}