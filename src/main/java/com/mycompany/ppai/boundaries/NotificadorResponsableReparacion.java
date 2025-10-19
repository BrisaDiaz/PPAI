package com.mycompany.ppai.boundaries;

import java.util.List;
import java.util.Objects;

public class NotificadorResponsableReparacion {

    private String cuerpoNotificacion;

    // Constructor
    public NotificadorResponsableReparacion() {
        // Inicialización si es necesario
    }

    // Métodos Setters
    public void setCuerpoNotificacion(String cuerpoNotificacion) {
        
        this.cuerpoNotificacion = Objects.requireNonNull(cuerpoNotificacion, "El cuerpo de la notificación no puede ser nulo");
    }

    // Métodos Getters
    public String getCuerpoNotificacion() {
        return cuerpoNotificacion;
    }
    
    public void enviarNotificacion(List<String> mails, String cuerpoNotificacion){
        setCuerpoNotificacion(cuerpoNotificacion);
        // Simulación de envío de notificación
        for (String mail : mails) {
            System.out.println("Enviando notificación a: " + mail);
            System.out.println("Cuerpo de la notificación: " + cuerpoNotificacion);
        }
        
    }
}