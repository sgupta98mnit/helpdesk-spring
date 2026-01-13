package com.sumit.helpdesk.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.outbox.poll-interval-ms:2000}")
    private long pollIntervalMs;

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:2000}")
    public void publish() {
        List<OutboxEvent> pending = outboxRepository.findTop20ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        for (OutboxEvent event : pending) {
            try {
                EventEnvelope envelope =
                        new EventEnvelope(event.getEventType(), event.getTenantId(), event.getPayloadJson());
                String payload = objectMapper.writeValueAsString(envelope);
                kafkaTemplate.send("helpdesk.events", event.getEventType(), payload);
                event.setStatus(OutboxStatus.PUBLISHED);
                event.setPublishedAt(Instant.now());
                outboxRepository.save(event);
            } catch (Exception ex) {
                event.setStatus(OutboxStatus.FAILED);
                outboxRepository.save(event);
            }
        }
    }
}
