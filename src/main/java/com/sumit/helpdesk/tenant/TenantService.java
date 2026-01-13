package com.sumit.helpdesk.tenant;

import com.sumit.helpdesk.auth.Role;
import com.sumit.helpdesk.auth.User;
import com.sumit.helpdesk.auth.UserRepository;
import com.sumit.helpdesk.common.ApiException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TenantService {
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TenantService(
            TenantRepository tenantRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public TenantResponse createTenant(TenantRequest request) {
        if (tenantRepository.existsBySlug(request.slug())) {
            throw new ApiException("Tenant slug already exists");
        }
        Tenant tenant = new Tenant();
        tenant.setSlug(request.slug());
        tenant.setName(request.name());
        Tenant saved = tenantRepository.save(tenant);

        User admin = new User();
        admin.setTenantId(saved.getSlug());
        admin.setEmail(request.adminEmail());
        admin.setPasswordHash(passwordEncoder.encode(request.adminPassword()));
        admin.setRole(Role.TENANT_ADMIN);
        userRepository.save(admin);
        return new TenantResponse(saved.getId().toString(), saved.getSlug(), saved.getName());
    }
}
