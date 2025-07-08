package ec.edu.espe.sincronizacion.service;

import ec.edu.espe.sincronizacion.dto.HoraClienteDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

@Service
public class SincronizacionService {
    private Map<String, Long> tiemposClientes = new HashMap<>();
    private static final int INTERVALO_SEGUNDOS = 10;

    public void registrarTiempoCliente(HoraClienteDto dto){
        tiemposClientes.put(dto.getNombreNodo(), dto.getHoraEnviada());
    }

    public void sincronizacionRelojes(){
        if(tiemposClientes.size() >= 2){
            long ahora = Instant.now().toEpochMilli();
            long promedio = (ahora + tiemposClientes.values().stream().mapToLong(Long::longValue).sum())
                    / (tiemposClientes.size() + 1);
            enviarAjuste(promedio);
            tiemposClientes.clear();
        }
    }

    public void enviarAjuste(long promedio){
        System.out.println("Enviar ajuste a los nodos: " + new Date(promedio));
    }
}
