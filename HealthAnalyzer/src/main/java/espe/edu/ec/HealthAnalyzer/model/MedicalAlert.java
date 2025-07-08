package espe.edu.ec.HealthAnalyzer.model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Data
@Entity
@Table(name = "medical_alerts")
public class MedicalAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alert_id", nullable = false)
    private String alertId;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "value", nullable = false)
    private Double value;

    @Column(name = "threshold", nullable = false)
    private Double threshold;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
}