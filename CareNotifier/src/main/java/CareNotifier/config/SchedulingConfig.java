package CareNotifier.config;

import CareNotifier.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "0 */30 * * * ?") // Cada 30 minutos
    public void scheduleLowPriorityNotifications() {
        notificationService.sendLowPriorityNotifications();
    }
}
