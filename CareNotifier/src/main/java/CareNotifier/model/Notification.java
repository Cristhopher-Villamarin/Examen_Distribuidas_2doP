package CareNotifier.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Data
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_id", nullable = false)
    private String notificationId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
}
