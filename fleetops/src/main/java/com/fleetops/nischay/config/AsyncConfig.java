package com.fleetops.nischay.config;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Slf4j
@Configuration
public class AsyncConfig {

    private ExecutorService tripPool;
    private ExecutorService analyticsPool;

    @Bean("tripExecutor")
    public ExecutorService tripExecutor() {
        tripPool = new ThreadPoolExecutor(
                5, 20,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new CustomThreadFactory("trip-worker"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        return tripPool;
    }

    @Bean("analyticsExecutor")
    public ExecutorService analyticsExecutor() {
        analyticsPool = new ThreadPoolExecutor(
                3, 5,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(50),
                new CustomThreadFactory("analytics-worker"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        return analyticsPool;
    }

    @PreDestroy
    public void shutdown() {
        shutdownPool("tripExecutor", tripPool);
        shutdownPool("analyticsExecutor", analyticsPool);
    }

    private void shutdownPool(String name, ExecutorService pool) {
        if (pool == null) return;
        log.info("Shutting down executor: {}", name);
        pool.shutdown();
        try {
            if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                log.warn("Forced shutdown of executor: {}", name);
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static class CustomThreadFactory implements ThreadFactory {
        private final String prefix;
        private int counter = 0;

        CustomThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, prefix + "-" + (++counter));
            t.setDaemon(false);
            t.setUncaughtExceptionHandler((thread, ex) ->
                    System.err.println("Uncaught exception in " + thread.getName() + ": " + ex.getMessage()));
            return t;
        }
    }
}