package healthanalyzer.service;

import healthanalyzer.dto.*;
import healthanalyzer.model.MedicalAlert;
import healthanalyzer.repository.MedicalAlertRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AlertService {
    @Autowired
    private MedicalAlertRepository medicalAlertRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
                    BloodPressureAlertDto alert = new BloodPressureAlertDto();
                    alert.setDeviceId(eventDto.getDeviceId());
                    alert.setValue(eventDto.getValue());
                    alert.setTimestamp(timestamp);
                    sendAlert(alert);
                    saveAlert(convertToMedicalAlert(alert));
                }
                break;
        }
    }

    private void sendAlert(Object alert) {
        try {
            String routingKey = alert instanceof CriticalHeartRateAlertDto ? "critical.heart.rate" :
                    alert instanceof OxygenLevelCriticalDto ? "oxygen.level.critical" :
                            alert instanceof BloodPressureAlertDto ? "blood.pressure.alert" :
                                    alert instanceof DailyReportGeneratedDto ? "daily.report" :
                                            alert instanceof DeviceOfflineAlertDto ? "device.offline" : null;
            if (routingKey != null) {
                rabbitTemplate.convertAndSend("vital-signs-exchange", routingKey, alert);
                retryCount = 0;
                String alertId = getAlertId(alert);
                offlineEvents.remove(alertId);
            }
        } catch (Exception e) {
            if (retryCount < 3) {
                long delay = (long) Math.pow(2, retryCount++) * 1000;
                offlineEvents.put(getAlertId(alert), alert);
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        sendAlert(alert);
                    }
                }, delay);
            }
        }
    }

    private String getAlertId(Object alert) {
        if (alert instanceof CriticalHeartRateAlertDto) {
            return ((CriticalHeartRateAlertDto) alert).getAlertId();
        } else if (alert instanceof OxygenLevelCriticalDto) {
            return ((OxygenLevelCriticalDto) alert).getAlertId();
        } else if (alert instanceof BloodPressureAlertDto) {
            return ((BloodPressureAlertDto) alert).getAlertId();
        } else if (alert instanceof DailyReportGeneratedDto) {
            return ((DailyReportGeneratedDto) alert).getReportId();
        } else if (alert instanceof DeviceOfflineAlertDto) {
            return ((DeviceOfflineAlertDto) alert).getAlertId();
        }
        return null;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Cada 24 horas a medianoche
    public void generateDailyReport() {
        String timestamp = Instant.now().toString();
        // Consulta para obtener dispositivos y tipos de signos vitales
        String sql = "SELECT device_id, type, AVG(value) as avg_value, MAX(value) as max_value, MIN(value) as min_value " +
                "FROM vital_signs WHERE timestamp >= ? GROUP BY device_id, type";
        Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS);
        List<DailyReportGeneratedDto> reports = jdbcTemplate.query(
                sql,
                new Object[]{yesterday.toString()},
                (rs, rowNum) -> {
                    DailyReportGeneratedDto report = new DailyReportGeneratedDto();
                    report.setDeviceId(rs.getString("device_id"));
                    report.setType(rs.getString("type"));
                    report.setAverage(rs.getDouble("avg_value"));
                    report.setMax(rs.getDouble("max_value"));
                    report.setMin(rs.getDouble("min_value"));
                    report.setTimestamp(timestamp);
                    return report;
                }
        );

        reports.forEach(this::sendAlert);
    }

    @Scheduled(cron = "0 0 */6 * * ?") // Cada 6 horas
    public void checkInactiveDevices() {
        String timestamp = Instant.now().toString();
        // Consulta para encontrar dispositivos sin lecturas en las últimas 24 horas
        String sql = "SELECT DISTINCT device_id FROM vital_signs WHERE timestamp < ? AND device_id NOT IN " +
                "(SELECT device_id FROM vital_signs WHERE timestamp >= ?)";
        Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS);
        List<String> inactiveDevices = jdbcTemplate.queryForList(
                sql,
                new Object[]{yesterday.toString(), yesterday.toString()},
                String.class
        );

        inactiveDevices.forEach(deviceId -> {
            DeviceOfflineAlertDto alert = new DeviceOfflineAlertDto();
            alert.setDeviceId(deviceId);
            alert.setTimestamp(timestamp);
            sendAlert(alert);
            saveAlert(convertToMedicalAlert(alert));
        });
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
        } else if (alert instanceof BloodPressureAlertDto) {
            BloodPressureAlertDto bpa = (BloodPressureAlertDto) alert;
            medicalAlert.setAlertId(bpa.getAlertId());
            medicalAlert.setType(bpa.getType());
            medicalAlert.setDeviceId(bpa.getDeviceId());
            medicalAlert.setValue(bpa.getValue());
            medicalAlert.setThreshold(bpa.getThreshold());
            medicalAlert.setTimestamp(bpa.getTimestamp());
        } else if (alert instanceof DeviceOfflineAlertDto) {
            DeviceOfflineAlertDto doa = (DeviceOfflineAlertDto) alert;
            medicalAlert.setAlertId(doa.getAlertId());
            medicalAlert.setType(doa.getType());
            medicalAlert.setDeviceId(doa.getDeviceId());
            medicalAlert.setValue(0.0); // No aplica
            medicalAlert.setThreshold(0.0); // No aplica
            medicalAlert.setTimestamp(doa.getTimestamp());
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