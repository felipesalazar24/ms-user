package cl.sanos_y_salvos.ms_base.api.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class NotificationConsumer {
    @RabbitListener(queues = "q.user.notification")
    public void receiveNotification(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            System.out.println("Procesando notificación asíncrona: " + message);
            
            channel.basicAck(tag, false);
        } catch (Exception e) {
            System.err.println("Error procesando notificación. Enviando a DLQ...");
            channel.basicNack(tag, false, false);
        }
    }
}
