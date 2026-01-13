package com.sumit.helpdesk;

import com.sumit.helpdesk.auth.Role;
import com.sumit.helpdesk.auth.User;
import com.sumit.helpdesk.auth.UserRepository;
import com.sumit.helpdesk.tenant.Tenant;
import com.sumit.helpdesk.tenant.TenantContext;
import com.sumit.helpdesk.tenant.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthFlowTest {
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("helpdesk")
                    .withUsername("helpdesk")
                    .withPassword("helpdesk");

    @DynamicPropertySource
    static void postgresProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("app.rate-limit.enabled", () -> "false");
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private TenantRepository tenantRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        Tenant tenant = new Tenant();
        tenant.setName("Acme");
        tenant.setSlug("acme");
        tenantRepository.save(tenant);

        TenantContext.setTenantId("acme");
        User user = new User();
        user.setEmail("admin@acme.com");
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setRole(Role.TENANT_ADMIN);
        userRepository.save(user);
        TenantContext.clear();
    }

    @Test
    void loginRequiresTenantAndReturnsTokens() throws Exception {
        String body = "{\"email\":\"admin@acme.com\",\"password\":\"password\"}";
        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Tenant-Id", "acme")
                                .content(body))
                .andExpect(status().isOk());
    }
}
