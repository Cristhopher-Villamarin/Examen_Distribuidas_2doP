package healthanalyzer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class MedicalAlert {
    @Id
    private String alertId;
    private String type;
    private String deviceId;
    private double value;
    private double threshold;
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

    public MedicalAlert(String alertId, String type, String deviceId, double value, double threshold, String timestamp) {
        this.alertId = alertId;
        this.type = type;
        this.deviceId = deviceId;
        this.value = value;
        this.threshold = threshold;
        this.timestamp = timestamp;
    }

    public MedicalAlert() {
    }
}
