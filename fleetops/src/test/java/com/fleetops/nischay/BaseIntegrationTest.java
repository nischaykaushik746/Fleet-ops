package com.fleetops.nischay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetops.nischay.repository.UserRepository;
import com.fleetops.nischay.role.RoleType;
import com.fleetops.nischay.security.JwtUtil;
import com.fleetops.nischay.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtUtil jwtUtil;

    protected User adminUser;
    protected User driverUser;
    protected User opsUser;

    protected String adminToken;
    protected String driverToken;
    protected String opsToken;

    @BeforeEach
    void setupUsers() {
        userRepository.deleteAll();

        adminUser = userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .roles(Set.of(RoleType.ADMIN))
                .build());

        driverUser = userRepository.save(User.builder()
                .username("driver")
                .password(passwordEncoder.encode("password"))
                .roles(Set.of(RoleType.DRIVER))
                .build());

        opsUser = userRepository.save(User.builder()
                .username("ops_manager")
                .password(passwordEncoder.encode("password"))
                .roles(Set.of(RoleType.OPS_MANAGER))
                .build());

        adminToken = jwtUtil.generateAccessToken(adminUser.getUsername(), adminUser.getId());
        driverToken = jwtUtil.generateAccessToken(driverUser.getUsername(), driverUser.getId());
        opsToken = jwtUtil.generateAccessToken(opsUser.getUsername(), opsUser.getId());
    }
}