package healthanalyzer.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class DailyReportGeneratedDto {
    private String reportId = UUID.randomUUID().toString();
    private String deviceId;
    private String type;
    private double average;
    private double max;
    private double min;
    private String timestamp;

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
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

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public DailyReportGeneratedDto(String reportId, String deviceId, String type, double average, double max, double min, String timestamp) {
        this.reportId = reportId;
        this.deviceId = deviceId;
        this.type = type;
        this.average = average;
        this.max = max;
        this.min = min;
        this.timestamp = timestamp;
    }

    public DailyReportGeneratedDto() {
    }
}
