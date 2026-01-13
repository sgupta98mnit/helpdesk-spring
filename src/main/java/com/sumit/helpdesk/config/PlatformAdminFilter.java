package com.sumit.helpdesk.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class PlatformAdminFilter extends OncePerRequestFilter {
    private static final String HEADER = "X-Platform-Admin-Key";

    private final String adminKey;

    public PlatformAdminFilter(
            @Value("${app.security.platform-admin-key:}") String adminKey) {
        this.adminKey = adminKey;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (requiresPlatformKey(request)) {
            if (!StringUtils.hasText(adminKey)) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return;
            }
            String provided = request.getHeader(HEADER);
            if (!adminKey.equals(provided)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean requiresPlatformKey(HttpServletRequest request) {
        return HttpMethod.POST.matches(request.getMethod())
                && "/tenants".equals(request.getRequestURI());
    }
}
