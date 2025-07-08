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
    public static final String BLOOD_PRESSURE_ALERT_QUEUE = "blood-pressure-alert-queue";
    public static final String DAILY_REPORT_QUEUE = "daily-report-queue";
    public static final String DEVICE_OFFLINE_QUEUE = "device-offline-queue";
    public static final String VITAL_SIGNS_EXCHANGE = "vital-signs-exchange";
    public static final String ROUTING_KEY_VITAL_SIGNS = "vital.signs";
    public static final String ROUTING_KEY_CRITICAL_HEART_RATE = "critical.heart.rate";
    public static final String ROUTING_KEY_OXYGEN_LEVEL = "oxygen.level.critical";
    public static final String ROUTING_KEY_BLOOD_PRESSURE = "blood.pressure.alert";
    public static final String ROUTING_KEY_DAILY_REPORT = "daily.report";
    public static final String ROUTING_KEY_DEVICE_OFFLINE = "device.offline";

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
    public Queue bloodPressureAlertQueue() {
        return new Queue(BLOOD_PRESSURE_ALERT_QUEUE, true);
    }

    @Bean
    public Queue dailyReportQueue() {
        return new Queue(DAILY_REPORT_QUEUE, true);
    }

    @Bean
    public Queue deviceOfflineQueue() {
        return new Queue(DEVICE_OFFLINE_QUEUE, true);
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
    public Binding bloodPressureAlertBinding(Queue bloodPressureAlertQueue, DirectExchange vitalSignsExchange) {
        return BindingBuilder.bind(bloodPressureAlertQueue).to(vitalSignsExchange).with(ROUTING_KEY_BLOOD_PRESSURE);
    }

    @Bean
    public Binding dailyReportBinding(Queue dailyReportQueue, DirectExchange vitalSignsExchange) {
        return BindingBuilder.bind(dailyReportQueue).to(vitalSignsExchange).with(ROUTING_KEY_DAILY_REPORT);
    }

    @Bean
    public Binding deviceOfflineBinding(Queue deviceOfflineQueue, DirectExchange vitalSignsExchange) {
        return BindingBuilder.bind(deviceOfflineQueue).to(vitalSignsExchange).with(ROUTING_KEY_DEVICE_OFFLINE);
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
