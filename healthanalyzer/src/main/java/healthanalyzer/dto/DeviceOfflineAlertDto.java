package healthanalyzer.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class DeviceOfflineAlertDto {
    private String alertId = UUID.randomUUID().toString();
    private String type = "DeviceOfflineAlert";
    private String deviceId;
    private String timestamp;

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public DeviceOfflineAlertDto(String alertId, String type, String deviceId, String timestamp) {
        this.alertId = alertId;
        this.type = type;
        this.deviceId = deviceId;
        this.timestamp = timestamp;
    }

    public DeviceOfflineAlertDto() {
    }
}
