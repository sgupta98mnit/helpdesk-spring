package com.sumit.helpdesk.webhooks;

import com.sumit.helpdesk.common.ApiException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WebhookService {
    private final WebhookRepository webhookRepository;
    private final WebhookDeliveryRepository deliveryRepository;
    private final RestTemplate restTemplate;

    public Webhook create(WebhookRequest request) {
        Webhook webhook = new Webhook();
        webhook.setUrl(request.url());
        webhook.setEvents(request.events());
        webhook.setSecret(request.secret());
        webhook.setEnabled(request.enabled());
        return webhookRepository.save(webhook);
    }

    public List<Webhook> list() {
        return webhookRepository.findAll();
    }

    public void delete(UUID id) {
        webhookRepository.deleteById(id);
    }

    public void enqueueDeliveries(String eventType, String payloadJson) {
        List<Webhook> webhooks = webhookRepository.findByEnabledTrue();
        for (Webhook webhook : webhooks) {
            if (!supportsEvent(webhook.getEvents(), eventType)) {
                continue;
            }
            WebhookDelivery delivery = new WebhookDelivery();
            delivery.setWebhookId(webhook.getId().toString());
            delivery.setEventType(eventType);
            delivery.setPayloadJson(payloadJson);
            deliveryRepository.save(delivery);
        }
    }

    public void attemptDelivery(WebhookDelivery delivery) {
        Webhook webhook = webhookRepository.findById(UUID.fromString(delivery.getWebhookId())).orElse(null);
        if (webhook == null || !webhook.isEnabled()) {
            delivery.setStatus(WebhookStatus.FAILED);
            deliveryRepository.save(delivery);
            return;
        }
        try {
            String signature = sign(webhook.getSecret(), delivery.getPayloadJson());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Helpdesk-Event", delivery.getEventType());
            headers.add("X-Helpdesk-Signature", signature);
            HttpEntity<String> request = new HttpEntity<>(delivery.getPayloadJson(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(webhook.getUrl(), request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                delivery.setStatus(WebhookStatus.SUCCESS);
            } else {
                scheduleRetry(delivery);
            }
        } catch (Exception ex) {
            scheduleRetry(delivery);
        }
        deliveryRepository.save(delivery);
    }

    private void scheduleRetry(WebhookDelivery delivery) {
        int attempts = delivery.getAttemptCount() + 1;
        delivery.setAttemptCount(attempts);
        if (attempts >= 5) {
            delivery.setStatus(WebhookStatus.FAILED);
            return;
        }
        delivery.setStatus(WebhookStatus.PENDING);
        long delayMinutes = (long) Math.pow(2, attempts);
        delivery.setNextAttemptAt(Instant.now().plus(delayMinutes, ChronoUnit.MINUTES));
    }

    private String sign(String secret, String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : raw) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw new ApiException("Failed to sign webhook");
        }
    }

    private boolean supportsEvent(String events, String eventType) {
        if (events == null || events.isBlank()) {
            return false;
        }
        for (String event : events.split(",")) {
            if (event.trim().equalsIgnoreCase(eventType)) {
                return true;
            }
        }
        return false;
    }
}
