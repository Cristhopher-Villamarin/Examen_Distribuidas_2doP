package ec.edu.espe.sincronizacion.config;

import ec.edu.espe.sincronizacion.service.SincronizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private SincronizacionService sincronizacionService;
    @Scheduled(fixedRate = 10000)
    public void ejecutarSincronizacion(){
        System.out.println("Ejecutando sincronización ....");
        sincronizacionService.sincronizacionRelojes();
    }
}
