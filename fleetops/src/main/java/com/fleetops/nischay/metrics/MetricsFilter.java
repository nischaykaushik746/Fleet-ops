package com.fleetops.nischay.metrics;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Order(2)
public class MetricsFilter implements Filter {

    private final MetricsService metricsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String key = req.getMethod() + " " + req.getRequestURI();
        metricsService.record(key);
        chain.doFilter(request, response);
    }
}