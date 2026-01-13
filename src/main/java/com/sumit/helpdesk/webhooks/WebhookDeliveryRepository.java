package com.sumit.helpdesk.webhooks;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WebhookDeliveryRepository extends JpaRepository<WebhookDelivery, UUID> {
    @Query(
            "select d from WebhookDelivery d where d.status = :status and "
                    + "(d.nextAttemptAt is null or d.nextAttemptAt <= :now)")
    List<WebhookDelivery> findReady(
            @Param("status") WebhookStatus status, @Param("now") Instant now);
}
