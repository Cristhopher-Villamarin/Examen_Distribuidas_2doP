package espe.edu.ec.HealthAnalyzer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String QUEUE_NAME = "vital-signs-queue";
    public static final String ALERT_EXCHANGE_NAME = "alerts-exchange";
    public static final String VITAL_SIGN_EXCHANGE_NAME = "vital-signs-exchange";
    public static final String ROUTING_KEY = "vital.signs";

    @Bean
    Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    TopicExchange vitalSignExchange() {
        return new TopicExchange(VITAL_SIGN_EXCHANGE_NAME);
    }

    @Bean
    TopicExchange alertExchange() {
        return new TopicExchange(ALERT_EXCHANGE_NAME);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange vitalSignExchange) {
        return BindingBuilder.bind(queue).to(vitalSignExchange).with(ROUTING_KEY);
    }
}
