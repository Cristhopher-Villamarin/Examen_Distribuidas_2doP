package espe.edu.ec.HealthAnalyzer.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalAlert {
    @Id
    private String alertId;
    private String type;
    private String deviceId;
    private double value;
    private double threshold;
    private LocalDateTime timestamp;
}