package healthanalyzer.service;

import healthanalyzer.dto.CriticalHeartRateAlertDto;
import healthanalyzer.dto.NewVitalSignEventDto;
import healthanalyzer.dto.OxygenLevelCriticalDto;
import healthanalyzer.model.MedicalAlert;
import healthanalyzer.repository.MedicalAlertRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AlertService {
    @Autowired
    private MedicalAlertRepository medicalAlertRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ConcurrentHashMap<String, Object> offlineEvents;

    private int retryCount = 0;

    @RabbitListener(queues = "vital-signs-queue")
    public void analyzeVitalSign(NewVitalSignEventDto eventDto) {
        // Aplicar reglas de negocio
        analyzeAndGenerateAlert(eventDto);

        // Registrar en auditoría (simulado)
        System.out.println("Evento analizado: " + eventDto);
    }

    private void analyzeAndGenerateAlert(NewVitalSignEventDto eventDto) {
        String timestamp = Instant.now().toString();
        switch (eventDto.getType()) {
            case "heart-rate":
                if (eventDto.getValue() > 140 || eventDto.getValue() < 40) {
                    CriticalHeartRateAlertDto alert = new CriticalHeartRateAlertDto();
                    alert.setDeviceId(eventDto.getDeviceId());
                    alert.setValue(eventDto.getValue());
                    alert.setTimestamp(timestamp);
                    sendAlert(alert);
                    saveAlert(convertToMedicalAlert(alert));
                }
                break;
            case "blood-oxygen":
                if (eventDto.getValue() < 90) {
                    OxygenLevelCriticalDto alert = new OxygenLevelCriticalDto();
                    alert.setDeviceId(eventDto.getDeviceId());
                    alert.setValue(eventDto.getValue());
                    alert.setTimestamp(timestamp);
                    sendAlert(alert);
                    saveAlert(convertToMedicalAlert(alert));
                }
                break;
            case "blood-pressure":
                if (eventDto.getValue() > 180) {
                    System.out.println("Alerta de presión arterial " + eventDto.getDeviceId());
                }
                break;
        }
    }

    private void sendAlert(Object alert) {
        try {
            String routingKey = alert instanceof CriticalHeartRateAlertDto ? "critical.heart.rate" :
                    alert instanceof OxygenLevelCriticalDto ? "oxygen.level.critical" : null;
            if (routingKey != null) {
                rabbitTemplate.convertAndSend("vital-signs-exchange", routingKey, alert);
                retryCount = 0;
                // Usar getAlertId() de manera genérica
                String alertId = (alert instanceof CriticalHeartRateAlertDto) ?
                        ((CriticalHeartRateAlertDto) alert).getAlertId() :
                        ((OxygenLevelCriticalDto) alert).getAlertId();
                offlineEvents.remove(alertId);
            }
        } catch (Exception e) {
            if (retryCount < 3) {
                long delay = (long) Math.pow(2, retryCount++) * 1000; // 1s, 2s, 4s
                offlineEvents.put(
                        (alert instanceof CriticalHeartRateAlertDto) ?
                                ((CriticalHeartRateAlertDto) alert).getAlertId() :
                                ((OxygenLevelCriticalDto) alert).getAlertId(),
                        alert
                );
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        sendAlert(alert);
                    }
                }, delay);
            }
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void resendOfflineEvents() {
        if (!offlineEvents.isEmpty()) {
            offlineEvents.forEach((alertId, alert) -> {
                if (alert instanceof CriticalHeartRateAlertDto || alert instanceof OxygenLevelCriticalDto) {
                    sendAlert(alert);
                    if (retryCount >= 3) {
                        offlineEvents.remove(alertId);
                    }
                }
            });
        }
    }

    private MedicalAlert convertToMedicalAlert(Object alert) {
        MedicalAlert medicalAlert = new MedicalAlert();
        if (alert instanceof CriticalHeartRateAlertDto) {
            CriticalHeartRateAlertDto chra = (CriticalHeartRateAlertDto) alert;
            medicalAlert.setAlertId(chra.getAlertId());
            medicalAlert.setType(chra.getType());
            medicalAlert.setDeviceId(chra.getDeviceId());
            medicalAlert.setValue(chra.getValue());
            medicalAlert.setThreshold(chra.getThreshold());
            medicalAlert.setTimestamp(chra.getTimestamp());
        } else if (alert instanceof OxygenLevelCriticalDto) {
            OxygenLevelCriticalDto olc = (OxygenLevelCriticalDto) alert;
            medicalAlert.setAlertId(olc.getAlertId());
            medicalAlert.setType(olc.getType());
            medicalAlert.setDeviceId(olc.getDeviceId());
            medicalAlert.setValue(olc.getValue());
            medicalAlert.setThreshold(olc.getThreshold());
            medicalAlert.setTimestamp(olc.getTimestamp());
        }
        return medicalAlert;
    }

    public void saveAlert(MedicalAlert alert) {
        medicalAlertRepository.save(alert);
    }

    public List<MedicalAlert> getAlertsByDeviceId(String deviceId) {
        return medicalAlertRepository.findByDeviceId(deviceId);
    }
}