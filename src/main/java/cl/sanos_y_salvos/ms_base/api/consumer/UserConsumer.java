package cl.sanos_y_salvos.ms_base.api.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class UserConsumer {

    private static final Logger LOGGER = Logger.getLogger(UserConsumer.class.getName());

    @RabbitListener(queues = "q.pet.command", ackMode = "MANUAL")
    public void consumeCommand(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            LOGGER.info("Procesando comando: " + message);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            LOGGER.severe("Error en comando. Enviando a DLQ: " + e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(queues = "q.user.notification", ackMode = "MANUAL")
    public void consumeNotification(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            LOGGER.info("Procesando notificación: " + message);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            LOGGER.severe("Error en notificación. Enviando a DLQ: " + e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(queues = "q.system.audit", ackMode = "MANUAL")
    public void consumeAudit(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            LOGGER.info("Procesando auditoría: " + message);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            LOGGER.severe("Error en auditoría. Enviando a DLQ: " + e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(queues = "q.pet.command.dlq")
    public void consumeCommandDlq(String message) {
        LOGGER.warning("[DLQ COMANDO] Mensaje fallido: " + message);
    }

    @RabbitListener(queues = "q.user.notification.dlq")
    public void consumeNotificationDlq(String message) {
        LOGGER.warning("[DLQ NOTIFICACION] Mensaje fallido: " + message);
    }

    @RabbitListener(queues = "q.system.audit.dlq")
    public void consumeAuditDlq(String message) {
        LOGGER.warning("[DLQ AUDITORIA] Mensaje fallido: " + message);
    }
}