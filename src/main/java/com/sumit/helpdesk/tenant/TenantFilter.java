package com.sumit.helpdesk.tenant;

import jakarta.persistence.EntityManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {
    private final EntityManager entityManager;

    @Value("${app.tenant.header:X-Tenant-Id}")
    private String tenantHeader;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String tenantId = request.getHeader(tenantHeader);
        if (requiresTenant(request) && (tenantId == null || tenantId.isBlank())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }
        if (tenantId != null && !tenantId.isBlank()) {
            TenantContext.setTenantId(tenantId);
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private boolean requiresTenant(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !(path.startsWith("/actuator")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger")
                || path.startsWith("/public")
                || path.startsWith("/auth")
                || path.startsWith("/tenants"));
    }
}
