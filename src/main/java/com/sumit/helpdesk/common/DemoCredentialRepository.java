package com.sumit.helpdesk.common;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoCredentialRepository extends JpaRepository<DemoCredential, UUID> {
    Optional<DemoCredential> findByTenantSlug(String tenantSlug);
}
