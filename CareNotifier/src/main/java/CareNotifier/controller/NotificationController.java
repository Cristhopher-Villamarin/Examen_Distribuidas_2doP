package CareNotifier.controller;

import CareNotifier.dto.MockNotificationRequest;
import CareNotifier.dto.NotificationResponse;
import CareNotifier.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping("/email")
    public ResponseEntity<NotificationResponse> mockEmail(@RequestBody MockNotificationRequest request) {
        return ResponseEntity.ok(service.sendMockNotification(request, "Email"));
    }

    @PostMapping("/sms")
    public ResponseEntity<NotificationResponse> mockSms(@RequestBody MockNotificationRequest request) {
        return ResponseEntity.ok(service.sendMockNotification(request, "SMS"));
    }
}
