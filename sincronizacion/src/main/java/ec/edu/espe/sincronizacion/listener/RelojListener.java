package ec.edu.espe.sincronizacion.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.espe.sincronizacion.dto.HoraClienteDto;
import ec.edu.espe.sincronizacion.service.SincronizacionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RelojListener {
    @Autowired
    private SincronizacionService sincronizacionService;

    @Autowired
    private  ObjectMapper mapper;

    @RabbitListener(queues = "reloj.solicitud")
    public void recibirSolicitud(String mensajeJson){
        try {
            HoraClienteDto dto=mapper.readValue(mensajeJson, HoraClienteDto.class);
            System.out.println("Recibo solicitud de reloj"+dto);
            sincronizacionService.registrarTiempoCliente(dto);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
