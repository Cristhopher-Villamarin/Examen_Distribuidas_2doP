package PatientDataCollector.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class VitalSignResponse {
    private Long id;
    private String deviceId;
    private String type;
    private Double value;
    private Instant timestamp;
}