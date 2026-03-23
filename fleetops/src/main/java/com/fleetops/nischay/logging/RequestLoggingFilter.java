package com.fleetops.nischay.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String traceId = UUID.randomUUID().toString();

        request.setAttribute("traceId", traceId);

        long start = System.currentTimeMillis();

        log.info("[{}] {} {}", traceId, req.getMethod(), req.getRequestURI());

        chain.doFilter(request, response);

        long time = System.currentTimeMillis() - start;

        if (time > 500) {
            log.warn("[{}] SLOW API took {} ms", traceId, time);
        } else {
            log.info("[{}] Completed in {} ms", traceId, time);
        }
    }
}