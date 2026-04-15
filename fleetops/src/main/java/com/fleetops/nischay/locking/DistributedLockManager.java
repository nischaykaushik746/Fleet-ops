package com.fleetops.nischay.locking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class DistributedLockManager {

    private final ConcurrentHashMap<String, DistributedLock> lockMap = new ConcurrentHashMap<>();
    private static final long LEASE_TIME_MS = 5000;

    public String acquireLock(String key) {
        String ownerId = UUID.randomUUID().toString();
        long expiry = System.currentTimeMillis() + LEASE_TIME_MS;

        DistributedLock newLock = new DistributedLock(ownerId, expiry);

        DistributedLock result = lockMap.compute(key, (k, existing) -> {
            if (existing == null || isExpired(existing)) {
                return newLock;
            }
            return existing;
        });

        if (result.getOwner().equals(ownerId)) {
            log.debug("Lock acquired | key={} owner={}", key, ownerId);
            return ownerId;
        }

        log.debug("Lock contention | key={} existingOwner={}", key, result.getOwner());
        return null;
    }

    public boolean releaseLock(String key, String ownerId) {
        boolean[] released = {false};

        lockMap.computeIfPresent(key, (k, existing) -> {
            if (existing.getOwner().equals(ownerId)) {
                released[0] = true;
                log.debug("Lock released | key={}", key);
                return null;
            }
            log.warn("Lock release rejected | key={} requestedBy={} ownedBy={}",
                    key, ownerId, existing.getOwner());
            return existing;
        });

        return released[0];
    }

    private boolean isExpired(DistributedLock lock) {
        return System.currentTimeMillis() > lock.getExpiryTime();
    }
}