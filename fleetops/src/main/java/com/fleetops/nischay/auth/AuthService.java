package com.fleetops.nischay.auth;

import com.fleetops.nischay.aop.TrackExecution;
import com.fleetops.nischay.repository.UserRepository;
import com.fleetops.nischay.role.RoleType;
import com.fleetops.nischay.security.JwtUtil;
import com.fleetops.nischay.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @TrackExecution
    public String signup(String username, String password) {

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(Set.of(RoleType.DRIVER))
                .build();

        userRepository.save(user);

        return "User created";
    }

    @TrackExecution
    public String login(User user) {
        return jwtUtil.generateToken(user.getUsername(), user.getId());
    }
}