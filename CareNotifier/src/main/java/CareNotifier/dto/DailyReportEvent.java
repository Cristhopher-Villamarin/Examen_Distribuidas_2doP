package CareNotifier.dto;

import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class DailyReportEvent {
    private String reportId;
    private String deviceId;
    private Map<String, VitalSignStats> stats;
    private Instant timestamp;

    @Data
    public static class VitalSignStats {
        private Double average;
        private Double max;
        private Double min;
    }
}
