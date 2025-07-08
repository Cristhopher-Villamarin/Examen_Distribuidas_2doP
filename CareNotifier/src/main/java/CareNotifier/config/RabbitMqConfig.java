package CareNotifier.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String ALERT_QUEUE_NAME = "alerts-queue";
    public static final String ALERT_EXCHANGE_NAME = "alerts-exchange";
    public static final String ROUTING_KEY = "alerts";

    @Bean
    Queue alertQueue() {
        return new Queue(ALERT_QUEUE_NAME, true);
    }

    @Bean
    TopicExchange alertExchange() {
        return new TopicExchange(ALERT_EXCHANGE_NAME);
    }

    @Bean
    Binding alertBinding(Queue alertQueue, TopicExchange alertExchange) {
        return BindingBuilder.bind(alertQueue).to(alertExchange).with(ROUTING_KEY);
    }
}
