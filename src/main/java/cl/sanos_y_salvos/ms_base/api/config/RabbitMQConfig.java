package cl.sanos_y_salvos.ms_base.api.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange commandExchange() {
        return new DirectExchange("ex.sanosysalvos.command");
    }

    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange("ex.sanosysalvos.events");
    }

    @Bean
    public Queue commandDlq() {
        return new Queue("q.pet.command.dlq", true);
    }

    @Bean
    public Queue commandQueue() {
        return QueueBuilder.durable("q.pet.command")
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "q.pet.command.dlq")
                .build();
    }

    @Bean
    public Binding commandBinding(Queue commandQueue, DirectExchange commandExchange) {
        return BindingBuilder.bind(commandQueue).to(commandExchange).with("pet.command");
    }

    @Bean
    public Queue notificationDlq() {
        return new Queue("q.user.notification.dlq", true);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable("q.user.notification")
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "q.user.notification.dlq")
                .withArgument("x-message-ttl", 60000) // Expiran y van a la DLQ en 60 segundos si no son consumidos
                .withArgument("x-max-length", 1000) // Límite máximo de mensajes en la cola
                .build();
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange eventExchange) {
        return BindingBuilder.bind(notificationQueue).to(eventExchange).with("notify.#");
    }

    @Bean
    public Queue auditDlq() {
        return new Queue("q.system.audit.dlq", true);
    }

    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable("q.system.audit")
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "q.system.audit.dlq")
                .build();
    }

    @Bean
    public Binding auditBinding(Queue auditQueue, TopicExchange eventExchange) {
        return BindingBuilder.bind(auditQueue).to(eventExchange).with("audit.#");
    }
}