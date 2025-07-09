package carenotifier.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableScheduling
public class ApplicationConfig {
    @Bean
    public ConcurrentHashMap<String, Object> pendingNotifications() {
        return new ConcurrentHashMap<>(); // Almacena notificaciones pendientes
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(); // Define el bean para RestTemplate
    }


}

