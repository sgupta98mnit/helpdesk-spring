package com.sumit.helpdesk.webhooks;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookRepository extends JpaRepository<Webhook, UUID> {
    List<Webhook> findByEnabledTrue();
}
