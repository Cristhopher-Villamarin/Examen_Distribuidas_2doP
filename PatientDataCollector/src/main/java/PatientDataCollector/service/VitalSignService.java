package patientdatacollector.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import patientdatacollector.config.RabbitMQConfig;
import patientdatacollector.dto.NewVitalSignEventDto;
import patientdatacollector.dto.VitalSignDto;
import patientdatacollector.model.VitalSign;
import patientdatacollector.repository.VitalSignRepository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class VitalSignService {
    @Autowired
    private VitalSignRepository vitalSignRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ConcurrentHashMap<String, Object> offlineEvents;

    private AtomicInteger eventCounter = new AtomicInteger(0);

    private AtomicInteger retryCount = new AtomicInteger(0);

    public void saveVitalSign(VitalSignDto dto) {
        // Validar datos
        validateVitalSign(dto);

        // Convertir DTO a entidad
        VitalSign vitalSign = new VitalSign();
        vitalSign.setDeviceId(dto.getDeviceId());
        vitalSign.setType(dto.getType());
        vitalSign.setValue(dto.getValue());
        vitalSign.setTimestamp(dto.getTimestamp());

        // Guardar en CockroachDB
        vitalSignRepository.save(vitalSign);

        // Preparar evento
        NewVitalSignEventDto eventDto = new NewVitalSignEventDto();
        String eventId = "EVT-" + String.format("%03d", eventCounter.incrementAndGet());
        eventDto.setEventId(eventId);
        eventDto.setDeviceId(dto.getDeviceId());
        eventDto.setType(dto.getType());
        eventDto.setValue(dto.getValue());
        eventDto.setTimestamp(dto.getTimestamp());

        // Intentar enviar evento a RabbitMQ
        sendEventWithRetry(eventDto);
    }

    private void validateVitalSign(VitalSignDto dto) {
        if (dto.getType() == null || !dto.getType().matches("heart-rate|blood-oxygen|blood-pressure")) {
            throw new IllegalArgumentException("Tipo de signo vital inválido. Debe ser: heart-rate, blood-oxygen o blood-pressure");
        }

        switch (dto.getType()) {
            case "heart-rate":
                if (dto.getValue() < 30 || dto.getValue() > 200) {
                    throw new IllegalArgumentException("Frecuencia cardíaca fuera de rango (30-200 bpm)");
                }
                break;

            case "blood-oxygen":
                if (dto.getValue() < 70 || dto.getValue() > 100) {
                    throw new IllegalArgumentException("Oxígeno en sangre fuera de rango (70-100%)");
                }
                break;

            case "blood-pressure":
                if (dto.getValue() < 70 || dto.getValue() > 190) {
                    throw new IllegalArgumentException("Presión arterial fuera de rango (70-190 mmHg)");
                }
                break;
        }
    }

    private void sendEventWithRetry(NewVitalSignEventDto eventDto) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, eventDto);
            retryCount.set(0);
            offlineEvents.remove(eventDto.getEventId());
        } catch (Exception e) {
            if (retryCount.get() < 3) {
                long delay = (long) Math.pow(2, retryCount.getAndIncrement()) * 1000;
                offlineEvents.put(eventDto.getEventId(), eventDto);
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        sendEventWithRetry(eventDto);
                    }
                }, delay);
            } else {
                offlineEvents.remove(eventDto.getEventId());
            }
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void resendOfflineEvents() {
        if (!offlineEvents.isEmpty()) {
            offlineEvents.forEach((eventId, event) -> {
                if (event instanceof NewVitalSignEventDto) {
                    sendEventWithRetry((NewVitalSignEventDto) event);
                }
            });
        }
    }

    public List<VitalSign> getVitalSignsByDeviceId(String deviceId) {
        return vitalSignRepository.findByDeviceId(deviceId);
    }
}
