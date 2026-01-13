package com.sumit.helpdesk.events;

public record EventEnvelope(String eventType, String tenantId, String payloadJson) {}
