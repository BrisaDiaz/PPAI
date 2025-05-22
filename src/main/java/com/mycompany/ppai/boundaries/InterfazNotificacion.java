package com.mycompany.ppai.boundaries;

import java.util.List;

public class InterfazNotificacion {

    // Constructor
    public InterfazNotificacion() {
        // Inicialización si es necesario
    }
    
    public void notificar(List<String> mails, String cuerpoNotificacion){
        // Simulación de envío de notificación
        for (String mail : mails) {
            System.out.println("Enviando notificación a: " + mail);
            System.out.println("Cuerpo de la notificación: " + cuerpoNotificacion);
        }
        
    }
}