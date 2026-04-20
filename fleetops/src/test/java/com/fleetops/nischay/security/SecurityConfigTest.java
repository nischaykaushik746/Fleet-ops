package com.fleetops.nischay.security;

import com.fleetops.nischay.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityConfigTest extends BaseIntegrationTest {

    @Test
    void publicEndpoints_shouldBeAccessible() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoints_withoutToken_shouldBeUnauthorized() throws Exception {
        mockMvc.perform(get("/analytics/deliveries"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpoints_withDriverToken_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/admin/metrics")
                        .header("Authorization", "Bearer " + driverToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoints_withAdminToken_shouldSucceed() throws Exception {
        mockMvc.perform(get("/admin/metrics")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void analytics_withOpsToken_shouldSucceed() throws Exception {
        mockMvc.perform(get("/analytics/deliveries")
                        .header("Authorization", "Bearer " + opsToken))
                .andExpect(status().isOk());
    }

    @Test
    void analytics_withDriverToken_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/analytics/deliveries")
                        .header("Authorization", "Bearer " + driverToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUserManagement_withAdminToken_shouldWork() throws Exception {
        mockMvc.perform(put("/admin/users/" + driverUser.getId() + "/role")
                        .param("role", "OPS_MANAGER")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}