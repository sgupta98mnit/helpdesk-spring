package com.sumit.helpdesk.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OutboxService(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    public void record(String eventType, Object payload) {
        OutboxEvent event = new OutboxEvent();
        event.setEventType(eventType);
        try {
            event.setPayloadJson(objectMapper.writeValueAsString(payload));
        } catch (Exception ex) {
            event.setPayloadJson("{}");
        }
        outboxRepository.save(event);
    }
}
