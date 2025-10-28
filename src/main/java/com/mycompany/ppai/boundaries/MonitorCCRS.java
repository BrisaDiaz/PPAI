package com.mycompany.ppai.boundaries;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MonitorCCRS implements IObservadorSismografo {
    private List<String> identificadorSismografo = new ArrayList<>();
    private List<LocalDateTime> fechasHoraActualizacion = new ArrayList<>();
    private List<String> estado= new ArrayList<>();
    private List<List<String>> motivos = new ArrayList<>();
    private List<List<String>> comentarios = new ArrayList<>();

    public void actualizar( String identificador,
                            LocalDateTime fechasHora,
                            String estado,
                            List<String> motivos,
            List<String> comentarios) {
        int indice = buscarSismografo(identificador);
        setIdentificadorSismografo(identificador, indice);
        setFechasHora(fechasHora, indice);
        setEstado(estado, indice);
        setMotivos(motivos, indice);
        setComentarios(comentarios, indice);
    }

    public void setIdentificadorSismografo(String identificador, int indice) {
        if (indice != -1) {
            this.identificadorSismografo.set(indice, identificador);
        } else {
            this.identificadorSismografo.add(identificador);
        }
    }
    
    public void setFechasHora(LocalDateTime fechasHora, int indice) {
        if (indice != -1) {
            this.fechasHoraActualizacion.set(indice, fechasHora);
        } else {
            this.fechasHoraActualizacion.add(fechasHora);
        }
    }

    public void setEstado(String estado, int indice) {
        if (indice != -1) {
            this.estado.set(indice, estado);
        } else {
            this.estado.add(estado);
        }
    }

    public void setMotivos(List<String> motivos, int indice) {
        if (indice != -1) {
            this.motivos.set(indice, motivos);
        } else {
            this.motivos.add(motivos);
        }
    }

    public void setComentarios(List<String> comentarios, int indice) {
        if (indice != -1) {
            this.comentarios.set(indice, comentarios);
        } else {
            this.comentarios.add(comentarios);
        }
    }
    
    public int buscarSismografo( String identificadorSismografo){
        int indice = -1;
        for ( int i = 0; i < this.identificadorSismografo.size(); i++ ) {
            if ( Objects.equals( this.identificadorSismografo.get(i), identificadorSismografo) ) {
                indice = i;
                break;
            }
        }
        return indice;
    }
}
