package com.sumit.helpdesk.tickets;

import com.sumit.helpdesk.common.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKey extends TenantEntity {
    @Column(nullable = false)
    private String keyValue;

    @Column(nullable = false)
    private String resourceId;
}
