package carenotifier.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Notification {
    @Id
    private String notificationId;
    private String eventType;
    private String recipient;
    private String status;
    private String timestamp;

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Notification(String notificationId, String eventType, String recipient, String status, String timestamp) {
        this.notificationId = notificationId;
        this.eventType = eventType;
        this.recipient = recipient;
        this.status = status;
        this.timestamp = timestamp;
    }

    public Notification() {
    }
}
