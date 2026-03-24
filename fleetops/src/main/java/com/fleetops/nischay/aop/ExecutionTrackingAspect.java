package com.fleetops.nischay.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExecutionTrackingAspect {

    @Around("@annotation(com.fleetops.nischay.aop.TrackExecution)")
    public Object trackExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();

            long time = System.currentTimeMillis() - start;

            if (time > 500) {
                log.warn("⚠️ SLOW METHOD: {} took {} ms", methodName, time);
            } else {
                log.info("✅ METHOD: {} executed in {} ms", methodName, time);
            }

            return result;

        } catch (Exception ex) {

            log.error("❌ ERROR in method: {}", methodName, ex);
            throw ex;
        }
    }
}