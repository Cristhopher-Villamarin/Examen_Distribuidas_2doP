package espe.edu.ec.HealthAnalyzer.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class DeviceOfflineAlertEvent {
    private String alertId;
    private String deviceId;
    private Instant timestamp;
}
