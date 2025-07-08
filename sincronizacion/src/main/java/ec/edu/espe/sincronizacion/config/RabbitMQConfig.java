package ec.edu.espe.sincronizacion.config;

import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue solicitudesReloj(){

        return QueueBuilder.durable("reloj.solicitud").build();
    }
}
