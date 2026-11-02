package cl.sanos_y_salvos.ms_base.api.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange("ex.sanosysalvos.events");
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
                .build();
    }

    @Bean
    public Binding notificationBinding (Queue notificationQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(notificationQueue).to(userExchange).with("notify.#");
    }

}
