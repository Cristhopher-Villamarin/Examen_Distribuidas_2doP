package espe.edu.ec.HealthAnalyzer.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class MedicalAlertEvent {
    private String alertId;
    private String type;
    private String deviceId;
    private Double value;
    private Double threshold;
    private Instant timestamp;
}
