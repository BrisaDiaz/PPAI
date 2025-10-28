package com.mycompany.ppai.boundaries;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificadorResponsableReparacion implements IObservadorSismografo {
    private List<String> emails;

    // Constructor
    public NotificadorResponsableReparacion(List<String> emails) {
        this.emails = emails;
    }

    public void actualizar( String identificador,
                            LocalDateTime fechasHora,
                            String estado,
                            List<String> motivos,
            List<String> comentarios) {

        String notificacion = generarNotificacion(identificador, fechasHora, estado, motivos, comentarios);
        // System.out.println(notificacion);
        enviarNotificacion(notificacion);
    }

    public String generarNotificacion(String identificadorSismografo, LocalDateTime fechasHora, String estado, List<String> motivos,
            List<String> comentarios) {

        StringBuilder cuerpo = new StringBuilder();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        String fechaHoraFormateada = fechasHora.format(formatter);

        cuerpo.append("Notificación de Estado del Sismógrafo\n");
        cuerpo.append("Fecha y Hora de Actualización: ").append(fechaHoraFormateada).append("\n");
        cuerpo.append("Identificador: ").append(identificadorSismografo).append("\n");
        cuerpo.append("Estado Actual: ").append(estado).append("\n");
        cuerpo.append("Motivos:\n");
        for (int i = 0; i < motivos.size(); i++) {
            cuerpo.append("- ").append(motivos.get(i)).append(": ");
            cuerpo.append(comentarios.get(i)).append("\n");
        }

        return cuerpo.toString();
    }

    public void enviarNotificacion(String notificacion){
       // Simulación de envío de notificación
    }
}