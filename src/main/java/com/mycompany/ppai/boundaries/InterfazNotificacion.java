package com.mycompany.ppai.boundaries;

public class InterfazNotificacion {
    
    public void notificar(String[] mails, String cuerpoNotificacion){
        // Simulación de envío de notificación
        for (String mail : mails) {
            System.out.println("Enviando notificación a: " + mail);
            System.out.println("Cuerpo de la notificación: " + cuerpoNotificacion);
        }
        
    }
}