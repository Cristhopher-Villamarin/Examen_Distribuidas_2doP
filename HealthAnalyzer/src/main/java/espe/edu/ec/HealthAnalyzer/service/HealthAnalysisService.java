package espe.edu.ec.HealthAnalyzer.service;


import espe.edu.ec.HealthAnalyzer.dto.AlertDTO;
import espe.edu.ec.HealthAnalyzer.dto.VitalSignDTO;
import espe.edu.ec.HealthAnalyzer.model.MedicalAlert;
import espe.edu.ec.HealthAnalyzer.repository.MedicalAlertRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class HealthAnalysisService {
    @Autowired
    private MedicalAlertRepository repository;

    @RabbitListener(queues = "vitalSignQueue")
    public void analyzeVitalSign(VitalSignDTO dto) {
        if (isCriticalCondition(dto)) {
            AlertDTO alert = generateAlert(dto);
            MedicalAlert alertEntity = new MedicalAlert(alert.getAlertId(), alert.getType(), alert.getDeviceId(), alert.getValue(), alert.getThreshold(), LocalDateTime.parse(alert.getTimestamp()));
            repository.save(alertEntity);
        }
    }

    private boolean isCriticalCondition(VitalSignDTO dto) {
        if ("heart-rate".equals(dto.getType())) {
            return dto.getValue() > 140 || dto.getValue() < 40;
        } else if ("oxygen".equals(dto.getType())) {
            return dto.getValue() < 90;
        } else if ("blood-pressure-systolic".equals(dto.getType()) || "blood-pressure-diastolic".equals(dto.getType())) {
            return dto.getValue() > 180 || dto.getValue() > 120;
        }
        return false;
    }

    private AlertDTO generateAlert(VitalSignDTO dto) {
        AlertDTO alert = new AlertDTO(UUID.randomUUID().toString(), "", dto.getDeviceId(), dto.getValue(), 0, dto.getTimestamp());
        if ("heart-rate".equals(dto.getType()) && (dto.getValue() > 140 || dto.getValue() < 40)) {
            alert.setType("CriticalHeartRateAlert");
            alert.setThreshold(140);
        } else if ("oxygen".equals(dto.getType()) && dto.getValue() < 90) {
            alert.setType("OxygenLevelCritical");
            alert.setThreshold(90);
        } else if ("blood-pressure-systolic".equals(dto.getType()) && dto.getValue() > 180) {
            alert.setType("HighBloodPressureAlert");
            alert.setThreshold(180);
        }
        return alert;
    }

}