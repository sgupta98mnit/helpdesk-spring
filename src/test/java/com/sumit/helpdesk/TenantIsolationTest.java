package com.sumit.helpdesk;

import com.sumit.helpdesk.tickets.Ticket;
import com.sumit.helpdesk.tickets.TicketRepository;
import com.sumit.helpdesk.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TenantIsolationTest {
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

    @Autowired private TicketRepository ticketRepository;
    @Autowired private EntityManager entityManager;

    @Test
    void tenantIsolationFiltersData() {
        TenantContext.setTenantId("tenant-a");
        enableTenantFilter("tenant-a");
        Ticket ticketA = new Ticket();
        ticketA.setTitle("A");
        ticketA.setDescription("A");
        ticketA.setRequesterEmail("a@example.com");
        ticketRepository.save(ticketA);

        TenantContext.clear();
        disableTenantFilter();

        TenantContext.setTenantId("tenant-b");
        enableTenantFilter("tenant-b");
        Ticket ticketB = new Ticket();
        ticketB.setTitle("B");
        ticketB.setDescription("B");
        ticketB.setRequesterEmail("b@example.com");
        ticketRepository.save(ticketB);

        Assertions.assertEquals(1, ticketRepository.findAll().size());

        TenantContext.clear();
        disableTenantFilter();
    }

    private void enableTenantFilter(String tenantId) {
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
    }

    private void disableTenantFilter() {
        Session session = entityManager.unwrap(Session.class);
        session.disableFilter("tenantFilter");
    }
}
