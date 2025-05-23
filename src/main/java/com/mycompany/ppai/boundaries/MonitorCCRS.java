package com.mycompany.ppai.boundaries;

import java.util.Objects;

public class MonitorCCRS {
    private String identificadorMonitor;
    private String cuerpoPublicacion;

    // Constructor
    public MonitorCCRS(String identificadorMonitor) {
        this.identificadorMonitor = identificadorMonitor;
    }
    
    // Métodos Setters
    public void setIdentificadorMonitor(String identificadorMonitor) {
        this.identificadorMonitor = Objects.requireNonNull(identificadorMonitor, "El identificador del monitor no puede ser nulo");
    }

    // Métodos Getters
    public String getIdentificadorMonitor() {
        return identificadorMonitor;
    }
    public String getCuerpoPublicacion() {
        return cuerpoPublicacion;
    }
    public void setCuerpoPublicacion(String cuerpoPublicacion) {
        this.cuerpoPublicacion = Objects.requireNonNull(cuerpoPublicacion, "El cuerpo de la publicación no puede ser nulo");
    }

    public void publicar( String cuerpoPublicacion){
    setCuerpoPublicacion(cuerpoPublicacion);

    // Simulación de publicación en Motinitor CCRS
    System.out.println("Publicando en Monitor CCRS: " + identificadorMonitor);
    System.out.println("Cuerpo de la publicación: " + cuerpoPublicacion);
    }
}
