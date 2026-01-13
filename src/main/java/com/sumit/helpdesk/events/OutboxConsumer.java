package com.sumit.helpdesk.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OutboxConsumer {
    private final ObjectMapper objectMapper;

    public OutboxConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "helpdesk.events", groupId = "helpdesk-consumer")
    public void consume(String message) {
        try {
            EventEnvelope envelope = objectMapper.readValue(message, EventEnvelope.class);
            log.info("Consumed event {} for tenant {}", envelope.eventType(), envelope.tenantId());
        } catch (Exception ex) {
            log.info("Consumed raw event {}", message);
        }
    }
}
