package com.fleetops.nischay.idempotency;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdempotencyService {

    private final ConcurrentHashMap<String, Object> store = new ConcurrentHashMap<>();

    public boolean isDuplicate(String key) {
        return store.containsKey(key);
    }

    public void save(String key, Object response) {
        store.put(key, response);
    }

    public Object get(String key) {
        return store.get(key);
    }
}