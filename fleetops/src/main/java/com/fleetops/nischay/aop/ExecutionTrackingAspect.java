package com.fleetops.nischay.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExecutionTrackingAspect {

    @Around("@annotation(com.fleetops.nischay.aop.TrackExecution)")
    public Object trackExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().toShortString();
        long start = System.nanoTime();

        try {
            Object result = joinPoint.proceed();
            long durationMs = (System.nanoTime() - start) / 1_000_000;

            if (durationMs > 1000) {
                log.warn("VERY SLOW: {} took {}ms", methodName, durationMs);
            } else if (durationMs > 500) {
                log.warn("SLOW: {} took {}ms", methodName, durationMs);
            } else {
                log.info("{} executed in {}ms", methodName, durationMs);
            }

            return result;

        } catch (Exception ex) {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            log.error("{} failed after {}ms: {}", methodName, durationMs, ex.getMessage());
            throw ex;
        }
    }
}