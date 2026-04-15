package com.fleetops.nischay.locking;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public final class DriverLockManager {

    private DriverLockManager() {}

    private static final ConcurrentHashMap<Long, ReentrantLock> LOCK_MAP = new ConcurrentHashMap<>();

    public static ReentrantLock getLock(Long driverId) {
        return LOCK_MAP.computeIfAbsent(driverId, k -> new ReentrantLock(true)); // fair lock
    }

    public static void removeLock(Long driverId) {
        ReentrantLock lock = LOCK_MAP.get(driverId);
        if (lock != null && !lock.isLocked()) {
            LOCK_MAP.remove(driverId);
        }
    }
}