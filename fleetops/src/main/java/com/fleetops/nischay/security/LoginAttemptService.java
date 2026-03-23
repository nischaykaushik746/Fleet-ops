package com.fleetops.nischay.security;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private final ConcurrentHashMap<String, Integer> attempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;

    public void loginFailed(String username) {
        attempts.merge(username, 1, Integer::sum);
    }

    public void loginSuccess(String username) {
        attempts.remove(username);
    }

    public boolean isBlocked(String username) {
        return attempts.getOrDefault(username, 0) >= MAX_ATTEMPTS;
    }
}