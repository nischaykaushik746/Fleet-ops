package com.fleetops.nischay.metrics;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

@Service
public class MetricsService {

    private final ConcurrentHashMap<String, LongAdder> apiHits = new ConcurrentHashMap<>();

    public void record(String api) {
        apiHits.computeIfAbsent(api, k -> new LongAdder()).increment();
    }

    public Map<String, Long> getStats() {
        return Collections.unmodifiableMap(
                apiHits.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().sum()))
        );
    }
}