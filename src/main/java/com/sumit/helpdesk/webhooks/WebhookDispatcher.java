package com.sumit.helpdesk.webhooks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sumit.helpdesk.events.EventEnvelope;
import com.sumit.helpdesk.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebhookDispatcher {
    private final WebhookService webhookService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "helpdesk.events", groupId = "helpdesk-webhooks")
    public void dispatch(String message) {
        try {
            EventEnvelope envelope = objectMapper.readValue(message, EventEnvelope.class);
            if (envelope.tenantId() != null) {
                TenantContext.setTenantId(envelope.tenantId());
            }
            webhookService.enqueueDeliveries(envelope.eventType(), envelope.payloadJson());
        } catch (Exception ex) {
            webhookService.enqueueDeliveries("event", message);
        } finally {
            TenantContext.clear();
        }
    }
}
