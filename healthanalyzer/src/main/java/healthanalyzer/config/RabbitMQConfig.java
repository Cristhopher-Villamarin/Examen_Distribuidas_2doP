package healthanalyzer.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String VITAL_SIGNS_QUEUE = "vital-signs-queue";
    public static final String CRITICAL_HEART_RATE_QUEUE = "critical-heart-rate-queue";
    public static final String OXYGEN_LEVEL_CRITICAL_QUEUE = "oxygen-level-critical-queue";
    public static final String VITAL_SIGNS_EXCHANGE = "vital-signs-exchange";
    public static final String ROUTING_KEY_VITAL_SIGNS = "vital.signs";
    public static final String ROUTING_KEY_CRITICAL_HEART_RATE = "critical.heart.rate";
    public static final String ROUTING_KEY_OXYGEN_LEVEL = "oxygen.level.critical";

    @Bean
    public Queue vitalSignsQueue() {
        return new Queue(VITAL_SIGNS_QUEUE, true);
    }

    @Bean
    public Queue criticalHeartRateQueue() {
        return new Queue(CRITICAL_HEART_RATE_QUEUE, true);
    }

    @Bean
    public Queue oxygenLevelCriticalQueue() {
        return new Queue(OXYGEN_LEVEL_CRITICAL_QUEUE, true);
    }

    @Bean
    public DirectExchange vitalSignsExchange() {
        return new DirectExchange(VITAL_SIGNS_EXCHANGE);
    }

    @Bean
    public Binding vitalSignsBinding(Queue vitalSignsQueue, DirectExchange vitalSignsExchange) {
        return BindingBuilder.bind(vitalSignsQueue).to(vitalSignsExchange).with(ROUTING_KEY_VITAL_SIGNS);
    }

    @Bean
    public Binding criticalHeartRateBinding(Queue criticalHeartRateQueue, DirectExchange vitalSignsExchange) {
        return BindingBuilder.bind(criticalHeartRateQueue).to(vitalSignsExchange).with(ROUTING_KEY_CRITICAL_HEART_RATE);
    }

    @Bean
    public Binding oxygenLevelCriticalBinding(Queue oxygenLevelCriticalQueue, DirectExchange vitalSignsExchange) {
        return BindingBuilder.bind(oxygenLevelCriticalQueue).to(vitalSignsExchange).with(ROUTING_KEY_OXYGEN_LEVEL);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
