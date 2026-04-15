package com.fleetops.nischay.idempotency;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class IdempotencyService {

    private static final long TTL_SECONDS = 3600; // 1 hour

    private final Map<String, CachedEntry> store = new ConcurrentHashMap<>();

    public boolean isDuplicate(String key) {
        CachedEntry entry = store.get(key);
        if (entry == null) return false;
        if (entry.isExpired()) {
            store.remove(key);
            return false;
        }
        return true;
    }

    public void save(String key, Object response) {
        store.put(key, new CachedEntry(response, Instant.now().plusSeconds(TTL_SECONDS)));
        log.debug("Idempotency key saved: {}", key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        CachedEntry entry = store.get(key);
        if (entry == null || entry.isExpired()) {
            if (entry != null) store.remove(key);
            return null;
        }
        return (T) entry.value;
    }

    //Periodic cleanup //
    public void cleanup() {
        Instant now = Instant.now();
        store.entrySet().removeIf(e -> e.getValue().isExpired());
    }

    private record CachedEntry(Object value, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}