package CareNotifier.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class NotificationResponse {
    private String notificationId;
    private String eventType;
    private String recipient;
    private String status;
    private Instant timestamp;
}
