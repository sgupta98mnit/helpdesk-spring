package com.sumit.helpdesk;

import com.sumit.helpdesk.events.OutboxEvent;
import com.sumit.helpdesk.events.OutboxPublisher;
import com.sumit.helpdesk.events.OutboxRepository;
import com.sumit.helpdesk.events.OutboxStatus;
import com.sumit.helpdesk.tenant.TenantContext;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OutboxKafkaTest {
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("helpdesk")
                    .withUsername("helpdesk")
                    .withPassword("helpdesk");

    @Container
    static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("app.rate-limit.enabled", () -> "false");
    }

    @Autowired private OutboxRepository outboxRepository;
    @Autowired private OutboxPublisher outboxPublisher;

    @Test
    void outboxPublishesToKafka() {
        TenantContext.setTenantId("tenant-test");
        OutboxEvent event = new OutboxEvent();
        event.setEventType("ticket.created");
        event.setPayloadJson("{\"id\":\"123\"}");
        event.setStatus(OutboxStatus.PENDING);
        outboxRepository.save(event);
        TenantContext.clear();

        outboxPublisher.publish();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList("helpdesk.events"));
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            Assertions.assertFalse(records.isEmpty());
        }
    }
}
