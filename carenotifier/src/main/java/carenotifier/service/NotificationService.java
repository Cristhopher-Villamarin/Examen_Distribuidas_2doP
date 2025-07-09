package carenotifier.service;

import carenotifier.dto.BloodPressureAlertDto;
import carenotifier.dto.CriticalHeartRateAlertDto;
import carenotifier.dto.DeviceOfflineAlertDto;
import carenotifier.dto.OxygenLevelCriticalDto;
import carenotifier.model.Notification;
import carenotifier.repository.NotificationRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ConcurrentHashMap<String, Object> pendingNotifications;

    private final List<Object> lowPriorityNotifications = new ArrayList<>();
    private int retryCount = 0;

    @RabbitListener(queues = {
            "critical-heart-rate-queue",
            "oxygen-level-critical-queue",
            "blood-pressure-alert-queue",
            "device-offline-queue"
    }, containerFactory = "rawRabbitListenerContainerFactory")
    public void processAlert(org.springframework.amqp.core.Message message) {
        System.out.println("[DEBUG] Mensaje recibido en processAlert: " + message);
        try {
            String typeId = (String) message.getMessageProperties().getHeaders().get("__TypeId__");
            Object alert = null;
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String json = new String(message.getBody());
            if ("healthanalyzer.dto.BloodPressureAlertDto".equals(typeId)) {
                alert = mapper.readValue(json, carenotifier.dto.BloodPressureAlertDto.class);
            } else if ("healthanalyzer.dto.CriticalHeartRateAlertDto".equals(typeId)) {
                alert = mapper.readValue(json, carenotifier.dto.CriticalHeartRateAlertDto.class);
            } else if ("healthanalyzer.dto.OxygenLevelCriticalDto".equals(typeId)) {
                alert = mapper.readValue(json, carenotifier.dto.OxygenLevelCriticalDto.class);
            } else if ("healthanalyzer.dto.DeviceOfflineAlertDto".equals(typeId)) {
                alert = mapper.readValue(json, carenotifier.dto.DeviceOfflineAlertDto.class);
            } else {
                System.err.println("[ERROR] Tipo de alerta desconocido: " + typeId);
                return;
            }
            String priority = classifyPriority(alert);
            if ("EMERGENCY".equals(priority)) {
                sendImmediateNotifications(alert);
                saveNotification(alert, "SENT");
            } else if ("WARNING".equals(priority)) {
                synchronized (lowPriorityNotifications) {
                    lowPriorityNotifications.add(alert);
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Error al deserializar el mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String classifyPriority(Object alert) {
        if (alert instanceof CriticalHeartRateAlertDto ||
                alert instanceof OxygenLevelCriticalDto ||
                alert instanceof BloodPressureAlertDto) {
            return "EMERGENCY";
        } else if (alert instanceof DeviceOfflineAlertDto) {
            return "WARNING";
        }
        return "INFO";
    }

    private void sendImmediateNotifications(Object alert) {
        String message = buildNotificationMessage(alert);
        sendEmailNotification(message);
        sendSmsNotification(message);
        sendPushNotification(message);
    }

    @Scheduled(cron = "0 */30 * * * ?") // Cada 30 minutos
    public void sendLowPriorityNotifications() {
        List<Object> notificationsToSend;
        synchronized (lowPriorityNotifications) {
            notificationsToSend = new ArrayList<>(lowPriorityNotifications);
            lowPriorityNotifications.clear();
        }
        notificationsToSend.forEach(alert -> {
            sendImmediateNotifications(alert);
            saveNotification(alert, "SENT");
        });
    }

    private String buildNotificationMessage(Object alert) {
        String alertId = getAlertId(alert);
        String deviceId = getDeviceId(alert);
        String type = getType(alert);
        String timestamp = getTimestamp(alert);
        return String.format("Alerta: %s, Dispositivo: %s, ID: %s, Timestamp: %s", type, deviceId, alertId, timestamp);
    }

    private void sendEmailNotification(String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(message, headers);
            restTemplate.postForObject("http://localhost:8080/mock/email", request, String.class);
            retryCount = 0;
        } catch (Exception e) {
            if (retryCount < 3) {
                long delay = (long) Math.pow(2, retryCount++) * 1000;
                pendingNotifications.put(UUID.randomUUID().toString(), message);
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        sendEmailNotification(message);
                    }
                }, delay);
            }
        }
    }

    private void sendSmsNotification(String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(message, headers);
            restTemplate.postForObject("http://localhost:8083/mock/sms", request, String.class);
            retryCount = 0;
        } catch (Exception e) {
            if (retryCount < 3) {
                long delay = (long) Math.pow(2, retryCount++) * 1000;
                pendingNotifications.put(UUID.randomUUID().toString(), message);
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        sendSmsNotification(message);
                    }
                }, delay);
            }
        }
    }

    private void sendPushNotification(String message) {
        System.out.println("Simulación de Push Notification: " + message);
    }

    @Scheduled(fixedDelay = 5000)
    public void resendPendingNotifications() {
        if (!pendingNotifications.isEmpty()) {
            pendingNotifications.forEach((id, message) -> {
                sendEmailNotification((String) message);
                sendSmsNotification((String) message);
                if (retryCount >= 3) {
                    pendingNotifications.remove(id);
                }
            });
        }
    }

    private void saveNotification(Object alert, String status) {
        System.out.println("[DEBUG] Intentando guardar notificación: " + alert + ", status: " + status);
        try {
            Notification notification = new Notification();
            notification.setNotificationId(UUID.randomUUID().toString());
            notification.setEventType(getType(alert));
            notification.setRecipient("medico@example.com"); // Simulado
            notification.setStatus(status);
            notification.setTimestamp(Instant.now().toString());
            notificationRepository.save(notification);
            System.out.println("[DEBUG] Notificación guardada exitosamente: " + notification);
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo guardar la notificación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getAlertId(Object alert) {
        if (alert instanceof CriticalHeartRateAlertDto) {
            return ((CriticalHeartRateAlertDto) alert).getAlertId();
        } else if (alert instanceof OxygenLevelCriticalDto) {
            return ((OxygenLevelCriticalDto) alert).getAlertId();
        } else if (alert instanceof BloodPressureAlertDto) {
            return ((BloodPressureAlertDto) alert).getAlertId();
        } else if (alert instanceof DeviceOfflineAlertDto) {
            return ((DeviceOfflineAlertDto) alert).getAlertId();
        }
        return "";
    }

    private String getDeviceId(Object alert) {
        if (alert instanceof CriticalHeartRateAlertDto) {
            return ((CriticalHeartRateAlertDto) alert).getDeviceId();
        } else if (alert instanceof OxygenLevelCriticalDto) {
            return ((OxygenLevelCriticalDto) alert).getDeviceId();
        } else if (alert instanceof BloodPressureAlertDto) {
            return ((BloodPressureAlertDto) alert).getDeviceId();
        } else if (alert instanceof DeviceOfflineAlertDto) {
            return ((DeviceOfflineAlertDto) alert).getDeviceId();
        }
        return "";
    }

    private String getType(Object alert) {
        if (alert instanceof CriticalHeartRateAlertDto) {
            return ((CriticalHeartRateAlertDto) alert).getType();
        } else if (alert instanceof OxygenLevelCriticalDto) {
            return ((OxygenLevelCriticalDto) alert).getType();
        } else if (alert instanceof BloodPressureAlertDto) {
            return ((BloodPressureAlertDto) alert).getType();
        } else if (alert instanceof DeviceOfflineAlertDto) {
            return ((DeviceOfflineAlertDto) alert).getType();
        }
        return "";
    }

    private String getTimestamp(Object alert) {
        if (alert instanceof CriticalHeartRateAlertDto) {
            return ((CriticalHeartRateAlertDto) alert).getTimestamp();
        } else if (alert instanceof OxygenLevelCriticalDto) {
            return ((OxygenLevelCriticalDto) alert).getTimestamp();
        } else if (alert instanceof BloodPressureAlertDto) {
            return ((BloodPressureAlertDto) alert).getTimestamp();
        } else if (alert instanceof DeviceOfflineAlertDto) {
            return ((DeviceOfflineAlertDto) alert).getTimestamp();
        }
        return "";
    }
}
