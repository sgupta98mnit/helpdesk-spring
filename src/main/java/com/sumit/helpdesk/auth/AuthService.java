package com.sumit.helpdesk.auth;

import com.sumit.helpdesk.common.ApiException;
import com.sumit.helpdesk.tenant.TenantContext;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final long refreshTtlDays;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            @Value("${app.security.jwt.refresh-token-ttl-days}") long refreshTtlDays) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTtlDays = refreshTtlDays;
    }

    public AuthResponse login(String email, String password) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new ApiException("Missing tenant context");
        }
        User user =
                userRepository
                        .findByEmailAndTenantId(email, tenantId)
                        .orElseThrow(() -> new ApiException("Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ApiException("Invalid credentials");
        }
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUserId(user.getId().toString());
        refreshToken.setExpiresAt(Instant.now().plus(refreshTtlDays, ChronoUnit.DAYS));
        refreshTokenRepository.save(refreshToken);
        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    public AuthResponse refresh(String refreshTokenValue) {
        String tenantId = TenantContext.getTenantId();
        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByTokenAndTenantId(refreshTokenValue, tenantId)
                        .orElseThrow(() -> new ApiException("Invalid refresh token"));
        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException("Expired refresh token");
        }
        User user =
                userRepository
                        .findById(UUID.fromString(refreshToken.getUserId()))
                        .orElseThrow(() -> new ApiException("User not found"));
        String accessToken = jwtService.generateAccessToken(user);
        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    public void logout(String refreshTokenValue) {
        String tenantId = TenantContext.getTenantId();
        refreshTokenRepository
                .findByTokenAndTenantId(refreshTokenValue, tenantId)
                .ifPresent(
                        token -> {
                            token.setRevoked(true);
                            refreshTokenRepository.save(token);
                        });
    }
}
