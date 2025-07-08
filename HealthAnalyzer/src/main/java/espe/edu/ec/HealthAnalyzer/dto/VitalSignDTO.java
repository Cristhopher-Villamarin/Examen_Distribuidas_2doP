package espe.edu.ec.HealthAnalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VitalSignDTO {
    private String deviceId;
    private String type;
    private double value;
    private String timestamp;
}