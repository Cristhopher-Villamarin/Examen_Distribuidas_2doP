package healthanalyzer.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class BloodPressureAlertDto {
    private String alertId = UUID.randomUUID().toString();
    private String type = "BloodPressureAlert";
    private String deviceId;
    private double value; // Temporal, para compatibilidad con el DTO actual
    private double threshold = 180.0;
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public BloodPressureAlertDto(String alertId, String type, String deviceId, double value, double threshold, String timestamp) {
        this.alertId = alertId;
        this.type = type;
        this.deviceId = deviceId;
        this.value = value;
        this.threshold = threshold;
        this.timestamp = timestamp;
    }

    public BloodPressureAlertDto() {
    }
}
