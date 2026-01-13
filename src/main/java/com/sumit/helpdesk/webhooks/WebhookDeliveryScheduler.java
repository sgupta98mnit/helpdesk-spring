package com.sumit.helpdesk.webhooks;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebhookDeliveryScheduler {
    private final WebhookDeliveryRepository deliveryRepository;
    private final WebhookService webhookService;

    @Scheduled(fixedDelay = 30000)
    public void deliver() {
        List<WebhookDelivery> deliveries =
                deliveryRepository.findReady(WebhookStatus.PENDING, Instant.now());
        for (WebhookDelivery delivery : deliveries) {
            webhookService.attemptDelivery(delivery);
        }
    }
}
