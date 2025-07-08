package notificaciones.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import notificaciones.dto.HoraClienteDto;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RelojProducer {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String nombreNodo = "Notificacion";

    public void enviarHora(){
        try {
            HoraClienteDto dto= new HoraClienteDto(nombreNodo, Instant.now().toEpochMilli());
            String json = objectMapper.writeValueAsString(dto);
            amqpTemplate.convertAndSend("reloj.solicitud",json);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}