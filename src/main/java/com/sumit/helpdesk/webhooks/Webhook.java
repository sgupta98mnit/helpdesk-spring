package com.sumit.helpdesk.webhooks;

import com.sumit.helpdesk.common.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "webhooks")
public class Webhook extends TenantEntity {
    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String events;

    @Column(nullable = false)
    private String secret;

    @Column(nullable = false)
    private boolean enabled = true;
}
