package com.mycompany.ppai.entities;

public enum NombreRolEnum {
    RESPONSABLE_DE_INSPECCION,
    RESPONSABLE_DE_REPARACION;

    public static NombreRolEnum fromString(String rol){
        return switch(rol.toLowerCase()){
            case "responsable de inspección" -> RESPONSABLE_DE_INSPECCION;
            case "responsable de reparación" -> RESPONSABLE_DE_REPARACION;
            default -> throw new IllegalArgumentException("Rol desconocido: " + rol);
        };
    }
}