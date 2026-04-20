package com.fleetops.nischay.locking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DistributedLockManagerTest {

    private DistributedLockManager lockManager;

    @BeforeEach
    void setUp() {
        lockManager = new DistributedLockManager();
    }

    @Test
    void acquireLock_shouldSucceed() {
        String owner = lockManager.acquireLock("driver:1");
        assertThat(owner).isNotNull();
    }

    @Test
    void acquireLock_shouldFailIfAlreadyLocked() {
        String owner1 = lockManager.acquireLock("driver:1");
        String owner2 = lockManager.acquireLock("driver:1");

        assertThat(owner1).isNotNull();
        assertThat(owner2).isNull();
    }

    @Test
    void releaseLock_shouldAllowReacquire() {
        String owner1 = lockManager.acquireLock("driver:1");
        assertThat(owner1).isNotNull();

        lockManager.releaseLock("driver:1", owner1);

        String owner2 = lockManager.acquireLock("driver:1");
        assertThat(owner2).isNotNull();
    }

    @Test
    void releaseLock_wrongOwner_shouldFail() {
        String owner = lockManager.acquireLock("driver:1");
        boolean released = lockManager.releaseLock("driver:1", "wrong-owner");
        assertThat(released).isFalse();
    }

    @Test
    void differentKeys_shouldNotConflict() {
        String o1 = lockManager.acquireLock("driver:1");
        String o2 = lockManager.acquireLock("driver:2");

        assertThat(o1).isNotNull();
        assertThat(o2).isNotNull();
    }
}