package com.fleetops.nischay.locking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class DistributedLockManager {

    private final ConcurrentHashMap<String, DistributedLock> lockMap = new ConcurrentHashMap<>();

    private static final long LEASE_TIME = 5000; // 5 sec

    public String acquireLock(String key) {

        String ownerId = UUID.randomUUID().toString();
        long expiry = System.currentTimeMillis() + LEASE_TIME;

        DistributedLock newLock = new DistributedLock(ownerId, expiry);

        lockMap.compute(key, (k, existing) -> {

            if (existing == null || isExpired(existing)) {
                return newLock;
            }

            return existing;
        });

        DistributedLock current = lockMap.get(key);

        if (current.getOwner().equals(ownerId)) {
            log.info("Lock acquired | key={} owner={}", key, ownerId);
            return ownerId;
        }

        return null;
    }

    public void releaseLock(String key, String ownerId) {

        lockMap.computeIfPresent(key, (k, existing) -> {

            if (existing.getOwner().equals(ownerId)) {
                log.info("Lock released | key={}", key);
                return null;
            }

            return existing;
        });
    }

    private boolean isExpired(DistributedLock lock) {
        return System.currentTimeMillis() > lock.getExpiryTime();
    }
}