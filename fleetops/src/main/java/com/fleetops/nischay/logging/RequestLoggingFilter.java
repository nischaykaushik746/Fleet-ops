package com.fleetops.nischay.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter implements Filter {

    private static final String TRACE_ID = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Use incoming trace ID or generate new one
        String traceId = req.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }

        MDC.put(TRACE_ID, traceId);
        res.setHeader("X-Trace-Id", traceId);

        long start = System.currentTimeMillis();

        log.info("→ {} {} (ip={})", req.getMethod(), req.getRequestURI(), getClientIp(req));

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;

            if (duration > 1000) {
                log.warn("← {} {} [{}] {}ms ⚠️ SLOW", req.getMethod(), req.getRequestURI(),
                        res.getStatus(), duration);
            } else {
                log.info("← {} {} [{}] {}ms", req.getMethod(), req.getRequestURI(),
                        res.getStatus(), duration);
            }

            MDC.remove(TRACE_ID);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) return xff.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}