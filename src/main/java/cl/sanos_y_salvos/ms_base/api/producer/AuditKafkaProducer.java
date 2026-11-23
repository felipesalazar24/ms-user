package cl.sanos_y_salvos.ms_base.api.producer;

import cl.sanos_y_salvos.ms_base.api.DTO.AuditEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuditKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(AuditKafkaProducer.class);
    public static final String TOPIC_ANALYTICS_AUDIT = "sanosysalvos.analytics.audit";

    private final KafkaTemplate<String, AuditEventDTO> kafkaTemplate;

    public AuditKafkaProducer(KafkaTemplate<String, AuditEventDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAuditEvent(AuditEventDTO event) {
        String key = event.userId() != null ? String.valueOf(event.userId()) : "unknown";
        kafkaTemplate.send(TOPIC_ANALYTICS_AUDIT, key, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Evento de auditoría enviado | topic={} partition={} offset={} eventType={} userId={}",
                                TOPIC_ANALYTICS_AUDIT,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                event.eventType(),
                                event.userId());
                    } else {
                        log.error("Error al enviar evento de auditoría | userId={} error={}", event.userId(), ex.getMessage());
                    }
                });
    }
}