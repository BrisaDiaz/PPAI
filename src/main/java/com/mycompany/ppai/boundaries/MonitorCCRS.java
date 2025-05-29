package com.mycompany.ppai.boundaries;

import java.util.Objects;

public class MonitorCCRS {
    private String cuerpoPublicacion;

    // Constructor
    public MonitorCCRS() {
    }
    
    // Métodos Setters
    public void setCuerpoPublicacion(String cuerpoPublicacion) {
        this.cuerpoPublicacion = Objects.requireNonNull(cuerpoPublicacion, "El cuerpo de la publicación no puede ser nulo");
    }

    // Métodos Getters
    public String getCuerpoPublicacion() {
        return cuerpoPublicacion;
    }

    public void publicar( String cuerpoPublicacion){
    setCuerpoPublicacion(cuerpoPublicacion);

    // Simulación de publicación en Motinitor CCRS
    System.out.println("Publicando en Monitor CCRS");
    System.out.println("Cuerpo de la publicación: " + cuerpoPublicacion);
    }
}
