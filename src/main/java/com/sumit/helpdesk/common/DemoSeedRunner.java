package com.sumit.helpdesk.common;

import com.sumit.helpdesk.auth.Role;
import com.sumit.helpdesk.auth.User;
import com.sumit.helpdesk.auth.UserRepository;
import com.sumit.helpdesk.tenant.Tenant;
import com.sumit.helpdesk.tenant.TenantContext;
import com.sumit.helpdesk.tenant.TenantRepository;
import java.security.SecureRandom;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoSeedRunner implements ApplicationRunner {
    private static final String TENANT_SLUG = "sumit";
    private static final String EMAIL = "test@sumit.com";

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DemoCredentialRepository demoCredentialRepository;

    public DemoSeedRunner(
            TenantRepository tenantRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            DemoCredentialRepository demoCredentialRepository) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.demoCredentialRepository = demoCredentialRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        demoCredentialRepository.deleteAll();
        DemoCredential demoCredential = new DemoCredential();
        demoCredential.setTenantSlug(TENANT_SLUG);
        demoCredential.setEmail(EMAIL);
        demoCredential.setPassword(generatePassword(12));
        demoCredentialRepository.save(demoCredential);

        Tenant tenant =
                tenantRepository
                        .findBySlug(TENANT_SLUG)
                        .orElseGet(
                                () -> {
                                    Tenant created = new Tenant();
                                    created.setSlug(TENANT_SLUG);
                                    created.setName("Sumit Demo");
                                    return tenantRepository.save(created);
                                });

        TenantContext.setTenantId(tenant.getSlug());
        try {
            User user =
                    userRepository
                            .findByEmailAndTenantId(EMAIL, TENANT_SLUG)
                            .orElseGet(
                                    () -> {
                                        User created = new User();
                                        created.setEmail(EMAIL);
                                        created.setRole(Role.TENANT_ADMIN);
                                        return created;
                                    });
            user.setPasswordHash(passwordEncoder.encode(demoCredential.getPassword()));
            userRepository.save(user);
        } finally {
            TenantContext.clear();
        }
    }

    private String generatePassword(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
