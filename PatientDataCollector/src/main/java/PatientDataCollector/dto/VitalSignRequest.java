package PatientDataCollector.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.time.Instant;

@Data
@Getter
@Setter
public class VitalSignRequest {
    @NotBlank(message = "deviceId is required")
    private String deviceId;

    @NotBlank(message = "type is required")
    private String type;

    @NotNull(message = "value is required")
    private Double value;

    @NotNull(message = "timestamp is required")
    private Instant timestamp;
}