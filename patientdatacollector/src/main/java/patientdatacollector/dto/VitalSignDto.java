package patientdatacollector.dto;

import lombok.Data;

@Data
public class VitalSignDto {
    private String deviceId;
    private String type; // heart-rate, blood-oxygen, blood-pressure
    private double value;
    private String timestamp;

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

    public VitalSignDto(String deviceId, String type, double value, String timestamp) {
        this.deviceId = deviceId;
        this.type = type;
        this.value = value;
        this.timestamp = timestamp;
    }

    public VitalSignDto() {
    }
}