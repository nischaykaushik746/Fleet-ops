package com.fleetops.nischay.locking;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DistributedLock {

    private final String owner;
    private final long expiryTime;
}