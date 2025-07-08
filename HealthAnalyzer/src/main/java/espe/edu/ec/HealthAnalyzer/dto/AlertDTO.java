package espe.edu.ec.HealthAnalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {
    private String alertId;
    private String type;
    private String deviceId;
    private double value;
    private double threshold;
    private String timestamp;
}