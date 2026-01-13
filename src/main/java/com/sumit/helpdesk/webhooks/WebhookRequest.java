package com.sumit.helpdesk.webhooks;

public record WebhookRequest(String url, String events, String secret, boolean enabled) {}
