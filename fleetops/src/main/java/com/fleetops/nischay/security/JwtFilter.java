package com.fleetops.nischay.security;

import com.fleetops.nischay.repository.UserRepository;
import com.fleetops.nischay.user.User;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(BEARER_PREFIX.length());

            // 1) Extract username
            String username = jwtUtil.extractUsername(token);

            if (username == null) {
                sendUnauthorized(response, "Invalid token: no subject");
                return;
            }

            // 2) Must be access token
            if (!jwtUtil.isAccessToken(token)) {
                sendUnauthorized(response, "Refresh tokens cannot be used for API access");
                return;
            }

            // 3) Check blacklist
            String tokenId = jwtUtil.extractTokenId(token);
            if (tokenBlacklistService.isBlacklisted(tokenId)) {
                sendUnauthorized(response, "Token has been revoked");
                return;
            }

            // 4) Check expiry
            if (jwtUtil.isTokenExpired(token)) {
                sendUnauthorized(response, "Token expired");
                return;
            }

            // 5) Set authentication if not already set
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByUsername(username).orElse(null);

                if (user == null || !user.isEnabled() || !user.isAccountNonLocked()) {
                    sendUnauthorized(response, "User account invalid");
                    return;
                }

                if (!jwtUtil.validateToken(token, user.getUsername())) {
                    sendUnauthorized(response, "Token validation failed");
                    return;
                }

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            log.warn("JWT processing error: {}", e.getMessage());
            sendUnauthorized(response, "Invalid token");
        } catch (Exception e) {
            log.error("Unexpected error in JWT filter", e);
            sendUnauthorized(response, "Authentication error");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/") || path.startsWith("/actuator/");
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}