package cl.sanos_y_salvos.ms_base.api.consumer;

import cl.sanos_y_salvos.ms_base.api.DTO.AuditEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuditKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(AuditKafkaConsumer.class);

    // Consumer Group 1: Auditoría de usuarios
    @KafkaListener(
            topics = "sanosysalvos.analytics.audit",
            groupId = "audit-group",
            containerFactory = "auditKafkaListenerContainerFactory"
    )
    public void consumeAuditEvent(
            @Payload AuditEventDTO event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        if (event == null || event.userId() == null || event.eventType() == null || event.eventType().isBlank()) {
            log.warn("Payload de auditoría inválido | partition={} offset={}", partition, offset);
            throw new IllegalArgumentException("Evento de auditoría inválido: faltan campos obligatorios");
        }

        log.info("Auditoría procesada [audit-group] | partition={} offset={} type={} userId={} email={} role={}",
                partition, offset, event.eventType(), event.userId(), event.email(), event.role());
    }

    // Consumer Group 2: Notificaciones a usuarios al recibir eventos de mascotas
    @KafkaListener(
            topics = "sanosysalvos.pets.events",
            groupId = "user-notifications-group",
            containerFactory = "petNotificationListenerContainerFactory"
    )
    public void consumePetEventForNotifications(
            @Payload Map<String, Object> petEvent,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        if (petEvent == null || !petEvent.containsKey("eventType")) {
            log.warn("Evento de mascota inválido en notificaciones | partition={} offset={}", partition, offset);
            throw new IllegalArgumentException("Evento de mascota inválido para notificaciones");
        }

        log.info("Notificación generada [user-notifications-group] | partition={} offset={} type={} petName={} userId={}",
                partition, offset, petEvent.get("eventType"), petEvent.get("petName"), petEvent.get("userId"));
    }
}