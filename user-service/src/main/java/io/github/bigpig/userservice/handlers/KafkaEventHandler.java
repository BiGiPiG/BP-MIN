package io.github.bigpig.userservice.handlers;

import io.github.bigpig.userservice.services.PresenceService;
import io.github.bigpig.userservice.services.ProfileService;
import io.github.bigpig.userservice.utils.GenericRecordConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventHandler {

    private final ProfileService profileService;
    private final PresenceService presenceService;

    @KafkaListener(
            topics = "user-created-event-topic",
            groupId = "${kafka.consumer.group-id:user-service-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserCreated(ConsumerRecord<String, Object> record) {
        Object value = record.value();

        if (value instanceof GenericRecord event) {
            var data = GenericRecordConverter.toCreateProfileRequest(event);
            profileService.createProfile(data);
        } else {
            log.error("Unexpected value type: {}",
                    value != null ? value.getClass().getName() : "null");
        }
    }

    @KafkaListener(
            topics = "user-changed-status-topic",
            groupId = "${kafka.consumer.group-id:user-service-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserChangedStatus(ConsumerRecord<String, Object> record) {
        Object value = record.value();

        if (value instanceof GenericRecord event) {
            String status = event.get("status").toString();
            String id = event.get("id").toString();
            log.info("Received UserChangedStatusEvent: id={}, status={}", id, status);

            presenceService.updateStatus(id, status);
        } else {
            log.error("Unexpected value type: {}",
                    value != null ? value.getClass().getName() : "null");
        }
    }
}