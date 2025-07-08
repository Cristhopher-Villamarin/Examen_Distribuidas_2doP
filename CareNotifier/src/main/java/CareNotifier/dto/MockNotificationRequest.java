package CareNotifier.dto;

import lombok.Data;

@Data
public class MockNotificationRequest {
    private String recipient;
    private String message;

    public MockNotificationRequest(String recipient, String message) {
    }
}
