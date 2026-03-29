package io.github.bigpig.userservice.handlers;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaEventHandler {

    @KafkaListener(
            topics = "user-created-event-topic",
            groupId = "${kafka.consumer.group-id:user-service-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserCreated(ConsumerRecord<String, Object> record) {
        Object value = record.value();

        if (value instanceof GenericRecord event) {
            String username = event.get("username").toString();
            String email = event.get("email").toString();
            Long id = (Long) event.get("id");

            log.info("📨 Received UserCreatedEvent: id={}, username={}, email={}", id, username, email);

        } else {
            log.error("⚠️ Unexpected value type: {}",
                    value != null ? value.getClass().getName() : "null");
        }
    }
}