package com.sumit.helpdesk.webhooks;

import com.sumit.helpdesk.common.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "webhook_deliveries")
public class WebhookDelivery extends TenantEntity {
    @Column(nullable = false)
    private String webhookId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "text")
    private String payloadJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookStatus status = WebhookStatus.PENDING;

    private int attemptCount = 0;

    private Instant nextAttemptAt;
}
