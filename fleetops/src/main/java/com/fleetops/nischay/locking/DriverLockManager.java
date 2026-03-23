package com.fleetops.nischay.locking;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class DriverLockManager {

    private static final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    public static ReentrantLock getLock(Long driverId) {
        lockMap.putIfAbsent(driverId, new ReentrantLock());
        return lockMap.get(driverId);
    }
}