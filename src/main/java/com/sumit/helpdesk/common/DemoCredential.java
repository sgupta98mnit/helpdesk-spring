package com.sumit.helpdesk.common;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "demo_credentials")
public class DemoCredential extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String tenantSlug;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
}
