package espe.edu.ec.HealthAnalyzer.service;

import espe.edu.ec.HealthAnalyzer.dto.DailyReportEvent;
import espe.edu.ec.HealthAnalyzer.dto.DeviceOfflineAlertEvent;
import espe.edu.ec.HealthAnalyzer.dto.MedicalAlertEvent;
import espe.edu.ec.HealthAnalyzer.dto.NewVitalSignEvent;
import espe.edu.ec.HealthAnalyzer.model.MedicalAlert;
import espe.edu.ec.HealthAnalyzer.repository.MedicalAlertRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HealthAnalyzerService {
    private final MedicalAlertRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final String alertExchangeName;

    public HealthAnalyzerService(MedicalAlertRepository repository, RabbitTemplate rabbitTemplate,
                                 @Value("${rabbitmq.alert.exchange.name}") String alertExchangeName) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        this.alertExchangeName = alertExchangeName;
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processVitalSignEvent(NewVitalSignEvent event) {
        MedicalAlertEvent alertEvent = null;

        // Reglas de negocio para detectar anomalías
        if ("heart-rate".equals(event.getType())) {
            if (event.getValue() > 140 || event.getValue() < 40) {
                alertEvent = createAlertEvent("CriticalHeartRateAlert", event, event.getValue() > 140 ? 140.0 : 40.0);
            }
        } else if ("oxygen".equals(event.getType()) && event.getValue() < 90) {
            alertEvent = createAlertEvent("OxygenLevelCritical", event, 90.0);
        } else if ("blood-pressure-systolic".equals(event.getType()) && event.getValue() > 180) {
            alertEvent = createAlertEvent("HighBloodPressureAlert", event, 180.0);
        } else if ("blood-pressure-diastolic".equals(event.getType()) && event.getValue() > 120) {
            alertEvent = createAlertEvent("HighBloodPressureAlert", event, 120.0);
        }

        if (alertEvent != null) {
            // Guardar alerta en CockroachDB
            MedicalAlert alert = new MedicalAlert();
            alert.setAlertId(alertEvent.getAlertId());
            alert.setType(alertEvent.getType());
            alert.setDeviceId(alertEvent.getDeviceId());
            alert.setValue(alertEvent.getValue());
            alert.setThreshold(alertEvent.getThreshold());
            alert.setTimestamp(alertEvent.getTimestamp());
            repository.save(alert);

            // Publicar alerta en RabbitMQ
            rabbitTemplate.convertAndSend(alertExchangeName, "alerts", alertEvent);
        }
    }

    private MedicalAlertEvent createAlertEvent(String type, NewVitalSignEvent event, Double threshold) {
        MedicalAlertEvent alert = new MedicalAlertEvent();
        alert.setAlertId("ALT-" + UUID.randomUUID().toString());
        alert.setType(type);
        alert.setDeviceId(event.getDeviceId());
        alert.setValue(event.getValue());
        alert.setThreshold(threshold);
        alert.setTimestamp(Instant.now());
        return alert;
    }

    public void generateDailyReport() {
        List<String> deviceIds = repository.findDistinctDeviceIds();
        Instant now = Instant.now();
        Instant yesterday = now.minusSeconds(24 * 60 * 60);

        for (String deviceId : deviceIds) {
            List<MedicalAlert> alerts = repository.findByDeviceIdAndTimestampAfter(deviceId, yesterday);
            Map<String, DailyReportEvent.VitalSignStats> stats = calculateStats(alerts);

            DailyReportEvent report = new DailyReportEvent();
            report.setReportId("RPT-" + UUID.randomUUID().toString());
            report.setDeviceId(deviceId);
            report.setStats(stats);
            report.setTimestamp(now);

            rabbitTemplate.convertAndSend(alertExchangeName, "reports", report);
        }
    }

    private Map<String, DailyReportEvent.VitalSignStats> calculateStats(List<MedicalAlert> alerts) {
        Map<String, List<Double>> valuesByType = alerts.stream()
                .collect(Collectors.groupingBy(
                        MedicalAlert::getType,
                        Collectors.mapping(MedicalAlert::getValue, Collectors.toList())
                ));

        Map<String, DailyReportEvent.VitalSignStats> stats = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : valuesByType.entrySet()) {
            List<Double> values = entry.getValue();
            DailyReportEvent.VitalSignStats stat = new DailyReportEvent.VitalSignStats();
            stat.setAverage(values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
            stat.setMax(values.stream().mapToDouble(Double::doubleValue).max().orElse(0.0));
            stat.setMin(values.stream().mapToDouble(Double::doubleValue).min().orElse(0.0));
            stats.put(entry.getKey(), stat);
        }
        return stats;
    }

    public void checkInactiveDevices() {
        Instant now = Instant.now();
        Instant last24Hours = now.minusSeconds(24 * 60 * 60);
        List<String> deviceIds = repository.findDistinctDeviceIds();

        for (String deviceId : deviceIds) {
            List<MedicalAlert> recentAlerts = repository.findByDeviceIdAndTimestampAfter(deviceId, last24Hours);
            if (recentAlerts.isEmpty()) {
                DeviceOfflineAlertEvent offlineAlert = new DeviceOfflineAlertEvent();
                offlineAlert.setAlertId("OFF-" + UUID.randomUUID().toString());
                offlineAlert.setDeviceId(deviceId);
                offlineAlert.setTimestamp(now);
                rabbitTemplate.convertAndSend(alertExchangeName, "alerts", offlineAlert);
            }
        }
    }
}