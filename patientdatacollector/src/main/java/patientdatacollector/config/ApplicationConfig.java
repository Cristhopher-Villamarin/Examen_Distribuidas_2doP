package patientdatacollector.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableScheduling
public class ApplicationConfig {

    @Bean
    public ConcurrentHashMap<String, Object> offlineEvents() {
        return new ConcurrentHashMap<>(); // Almacena eventos en memoria durante fallos de RabbitMQ
    }
}