package CareNotifier.service;

import CareNotifier.dto.*;
import CareNotifier.repository.NotificationRepository;

import CareNotifier.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final String alertExchangeName;
    private final List<Object> pendingLowPriorityEvents = new ArrayList<>();

    public NotificationService(NotificationRepository repository, RabbitTemplate rabbitTemplate,
                               @Value("${rabbitmq.alert.exchange.name}") String alertExchangeName) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        this.alertExchangeName = alertExchangeName;
    }

    @RabbitListener(queues = "${rabbitmq.alert.queue.name}")
    public void processAlertEvent(Object event) {
        String eventType = determineEventType(event);
        String recipient = "doctor@example.com"; // Simulado, puede configurarse vía Spring Cloud Config
        String gravity = determineGravity(eventType);

        if ("EMERGENCY".equals(gravity)) {
            sendNotification(event, recipient, eventType);
        } else if ("INFO".equals(gravity)) {
            synchronized (pendingLowPriorityEvents) {
                pendingLowPriorityEvents.add(event);
            }
        }
    }

    private String determineEventType(Object event) {
        if (event instanceof MedicalAlertEvent) {
            return ((MedicalAlertEvent) event).getType();
        } else if (event instanceof DeviceOfflineAlertEvent) {
            return "DeviceOfflineAlert";
        } else if (event instanceof DailyReportEvent) {
            return "DailyReportGenerated";
        }
        return "Unknown";
    }

    private String determineGravity(String eventType) {
        switch (eventType) {
            case "CriticalHeartRateAlert":
            case "OxygenLevelCritical":
            case "HighBloodPressureAlert":
                return "EMERGENCY";
            case "DeviceOfflineAlert":
            case "DailyReportGenerated":
                return "INFO";
            default:
                return "WARNING";
        }
    }

    public void sendNotification(Object event, String recipient, String eventType) {
        String notificationId = "NOT-" + UUID.randomUUID().toString();
        String message = buildNotificationMessage(event);
        String status = "SENT";

        try {
            // Simular correo electrónico (log)
            logger.info("Email sent to {}: {}", recipient, message);

            // Simular SMS (endpoint dummy)
            rabbitTemplate.convertAndSend(alertExchangeName, "mock.sms", new MockNotificationRequest(recipient, message));

            // Simular push notification (consola)
            System.out.println("Push notification: " + message);

        } catch (Exception e) {
            status = "FAILED";
            logger.error("Failed to send notification: {}", e.getMessage());
            // Almacenar evento pendiente para reintento
            synchronized (pendingLowPriorityEvents) {
                pendingLowPriorityEvents.add(event);
            }
        }

        // Guardar notificación en CockroachDB
        Notification notification = new Notification();
        notification.setNotificationId(notificationId);
        notification.setEventType(eventType);
        notification.setRecipient(recipient);
        notification.setStatus(status);
        notification.setTimestamp(Instant.now());
        repository.save(notification);
    }

    private String buildNotificationMessage(Object event) {
        if (event instanceof MedicalAlertEvent alert) {
            return String.format("Alert %s for device %s: value=%.2f, threshold=%.2f at %s",
                    alert.getType(), alert.getDeviceId(), alert.getValue(), alert.getThreshold(), alert.getTimestamp());
        } else if (event instanceof DeviceOfflineAlertEvent offline) {
            return String.format("Device %s offline at %s", offline.getDeviceId(), offline.getTimestamp());
        } else if (event instanceof DailyReportEvent report) {
            return String.format("Daily report for device %s at %s: %s", report.getDeviceId(), report.getTimestamp(), report.getStats());
        }
        return "Unknown event";
    }

    public void sendLowPriorityNotifications() {
        List<Object> eventsToProcess;
        synchronized (pendingLowPriorityEvents) {
            eventsToProcess = new ArrayList<>(pendingLowPriorityEvents);
            pendingLowPriorityEvents.clear();
        }

        for (Object event : eventsToProcess) {
            String eventType = determineEventType(event);
            if ("INFO".equals(determineGravity(eventType))) {
                sendNotification(event, "doctor@example.com", eventType);
            }
        }
    }

    public NotificationResponse sendMockNotification(MockNotificationRequest request, String type) {
        String notificationId = "NOT-" + UUID.randomUUID().toString();
        String status = "SENT";
        logger.info("{} notification sent to {}: {}", type, request.getRecipient(), request.getMessage());

        Notification notification = new Notification();
        notification.setNotificationId(notificationId);
        notification.setEventType(type);
        notification.setRecipient(request.getRecipient());
        notification.setStatus(status);
        notification.setTimestamp(Instant.now());
        repository.save(notification);

        NotificationResponse response = new NotificationResponse();
        response.setNotificationId(notificationId);
        response.setEventType(type);
        response.setRecipient(request.getRecipient());
        response.setStatus(status);
        response.setTimestamp(notification.getTimestamp());
        return response;
    }
}
