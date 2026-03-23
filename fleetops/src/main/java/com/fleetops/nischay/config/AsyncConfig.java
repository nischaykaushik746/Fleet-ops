package com.fleetops.nischay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class AsyncConfig {

    @Bean("tripExecutor")
    public ExecutorService tripExecutor() {
        return new ThreadPoolExecutor(
                5,
                20,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                r -> {
                    Thread t = new Thread(r);
                    t.setName("trip-worker-" + t.getId());
                    return t;
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Bean("analyticsExecutor")
    public ExecutorService analyticsExecutor() {
        return Executors.newFixedThreadPool(5);
    }
}