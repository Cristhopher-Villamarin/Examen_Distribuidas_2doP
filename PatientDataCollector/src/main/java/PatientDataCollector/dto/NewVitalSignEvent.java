package PatientDataCollector.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class NewVitalSignEvent {
    private String eventId;
    private String deviceId;
    private String type;
    private Double value;
    private Instant timestamp;
}