package com.fleetops.nischay.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TokenBlacklistService {

    // tokenId -> expiry time
    private final Map<String, Date> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String tokenId, Date expiry) {
        blacklist.put(tokenId, expiry);
        log.info("Token blacklisted: {}", tokenId);
    }

    public boolean isBlacklisted(String tokenId) {
        if (!blacklist.containsKey(tokenId)) return false;

        Date expiry = blacklist.get(tokenId);
        if (expiry.before(new Date())) {
            blacklist.remove(tokenId);
            return false;
        }

        return true;
    }

    // Periodic cleanup of expired entries //
    public void cleanup() {
        Date now = new Date();
        blacklist.entrySet().removeIf(e -> e.getValue().before(now));
    }
}