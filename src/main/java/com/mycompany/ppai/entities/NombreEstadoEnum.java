package com.mycompany.ppai.entities;

public enum NombreEstadoEnum {
    COMPLETAMENTE_REALIZADA,
    CERRADA,
    FUERA_DE_SERVICIO,
    ONLINE;

    public static NombreEstadoEnum fromString(String estado){
        return switch(estado.toLowerCase()){
            case "completamente realizada" -> COMPLETAMENTE_REALIZADA;
            case "cerrada" -> CERRADA;
            case "fuera de servicio" -> FUERA_DE_SERVICIO;
            case "online" -> ONLINE;
            default -> throw new IllegalArgumentException("Estado desconocido: " + estado);
        };
    }
}