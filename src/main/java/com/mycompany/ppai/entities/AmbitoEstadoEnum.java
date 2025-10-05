package com.mycompany.ppai.entities;

public enum AmbitoEstadoEnum {
    ORDEN_DE_INSPECCION,
    SISMOGRAFO;

    public static AmbitoEstadoEnum fromString(String ambito){
        return switch(ambito.toLowerCase()){
            case "orden de inspección" -> ORDEN_DE_INSPECCION;
            case "sismógrafo" -> SISMOGRAFO;
            default -> throw new IllegalArgumentException("Ambito desconocido: " + ambito);
        };
    }
}