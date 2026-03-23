package com.fleetops.nischay.security;

import com.fleetops.nischay.repository.UserRepository;
import com.fleetops.nischay.role.RoleType;
import com.fleetops.nischay.user.User;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByUsername(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .username(email)
                                .roles(Set.of(RoleType.DRIVER))
                                .build()
                ));

        String token = jwtUtil.generateToken(user.getUsername(), user.getId());

        response.setContentType("application/json");
        response.getWriter().write("{\"token\":\"" + token + "\"}");
    }
}