package patientdatacollector.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class NewVitalSignEventDto {
    private String eventId = UUID.randomUUID().toString();
    private String deviceId;
    private String type;
    private double value;
    private String timestamp;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public NewVitalSignEventDto(String eventId, String deviceId, String type, double value, String timestamp) {
        this.eventId = eventId;
        this.deviceId = deviceId;
        this.type = type;
        this.value = value;
        this.timestamp = timestamp;
    }

    public NewVitalSignEventDto() {
    }
}
