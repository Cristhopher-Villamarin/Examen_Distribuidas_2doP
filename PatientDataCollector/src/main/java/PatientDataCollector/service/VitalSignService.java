package PatientDataCollector.service;

import PatientDataCollector.dto.NewVitalSignEvent;
import PatientDataCollector.dto.VitalSignRequest;
import PatientDataCollector.dto.VitalSignResponse;
import PatientDataCollector.model.VitalSign;
import PatientDataCollector.repository.VitalSignRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VitalSignService {
    private final VitalSignRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;

    public VitalSignService(VitalSignRepository repository, RabbitTemplate rabbitTemplate,
                            @Value("${rabbitmq.exchange.name}") String exchangeName) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
    }

    public VitalSignResponse saveVitalSign(VitalSignRequest request) {
        // Validar rangos de frecuencia cardíaca
        if ("heart-rate".equals(request.getType()) && (request.getValue() < 30 || request.getValue() > 200)) {
            throw new IllegalArgumentException("Heart rate must be between 30 and 200");
        }

        // Mapear DTO a entidad
        VitalSign vitalSign = new VitalSign();
        vitalSign.setDeviceId(request.getDeviceId());
        vitalSign.setType(request.getType());
        vitalSign.setValue(request.getValue());
        vitalSign.setTimestamp(request.getTimestamp());

        // Guardar en CockroachDB
        VitalSign saved = repository.save(vitalSign);

        // Publicar evento en RabbitMQ
        NewVitalSignEvent event = new NewVitalSignEvent();
        event.setEventId("EVT-" + UUID.randomUUID().toString());
        event.setDeviceId(saved.getDeviceId());
        event.setType(saved.getType());
        event.setValue(saved.getValue());
        event.setTimestamp(saved.getTimestamp());
        rabbitTemplate.convertAndSend(exchangeName, "vital.signs", event);

        // Mapear entidad a respuesta
        VitalSignResponse response = new VitalSignResponse();
        response.setId(saved.getId());
        response.setDeviceId(saved.getDeviceId());
        response.setType(saved.getType());
        response.setValue(saved.getValue());
        response.setTimestamp(saved.getTimestamp());
        return response;
    }

    public List<VitalSignResponse> getVitalSignsByDeviceId(String deviceId) {
        return repository.findByDeviceId(deviceId).stream()
                .map(vs -> {
                    VitalSignResponse response = new VitalSignResponse();
                    response.setId(vs.getId());
                    response.setDeviceId(vs.getDeviceId());
                    response.setType(vs.getType());
                    response.setValue(vs.getValue());
                    response.setTimestamp(vs.getTimestamp());
                    return response;
                })
                .collect(Collectors.toList());
    }
}