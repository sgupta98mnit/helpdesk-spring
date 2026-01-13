package com.sumit.helpdesk.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class DemoCredentialsController {
    private final DemoCredentialRepository repository;

    public DemoCredentialsController(DemoCredentialRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/demo-credentials")
    public ResponseEntity<DemoCredentials> get() {
        return repository
                .findByTenantSlug("sumit")
                .map(
                        entity ->
                                ResponseEntity.ok(
                                        new DemoCredentials(
                                                entity.getTenantSlug(),
                                                entity.getEmail(),
                                                entity.getPassword())))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
