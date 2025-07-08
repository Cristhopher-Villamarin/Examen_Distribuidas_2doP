package espe.edu.ec.HealthAnalyzer.config;

import espe.edu.ec.HealthAnalyzer.service.HealthAnalyzerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    @Autowired
    private HealthAnalyzerService healthAnalyzerService;

    @Scheduled(cron = "0 0 0 * * ?") // Cada 24 horas a medianoche
    public void scheduleDailyReport() {
        healthAnalyzerService.generateDailyReport();
    }

    @Scheduled(cron = "0 0 */6 * * ?") // Cada 6 horas
    public void scheduleInactiveDevicesCheck() {
        healthAnalyzerService.checkInactiveDevices();
    }
}
