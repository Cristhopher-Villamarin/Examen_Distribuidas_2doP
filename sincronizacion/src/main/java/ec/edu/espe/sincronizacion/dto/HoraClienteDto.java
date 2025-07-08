package ec.edu.espe.sincronizacion.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class HoraClienteDto {
    private String nombreNodo;
    private long horaEnviada;

    public String getNombreNodo() {
        return nombreNodo;
    }

    public void setNombreNodo(String nombreNodo) {
        this.nombreNodo = nombreNodo;
    }

    public long getHoraEnviada() {
        return horaEnviada;
    }

    public void setHoraEnviada(long horaEnviada) {
        this.horaEnviada = horaEnviada;
    }

    public HoraClienteDto(String nombreNodo, long horaEnviada) {
        this.nombreNodo = nombreNodo;
        this.horaEnviada = horaEnviada;
    }

    public HoraClienteDto() {
    }
}
