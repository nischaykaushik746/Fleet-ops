package com.fleetops.nischay.metrics;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class MetricsService {

    private final ConcurrentHashMap<String, Long> apiHits = new ConcurrentHashMap<>();

    public void record(String api) {
        apiHits.merge(api, 1L, Long::sum);
    }

    public ConcurrentHashMap<String, Long> getStats() {
        return apiHits;
    }
}