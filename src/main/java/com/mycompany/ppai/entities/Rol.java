package com.mycompany.ppai.entities;

import java.util.Objects; // Importar Objects para usar requireNonNull

public class Rol {
    private String nombre;
    private String descripcionRol;

    /**
     * Constructor de la clase Rol.
     * @param nombre       El nombre del rol.
     * @param descripcionRol La descripción del rol.
     */
    public Rol(String nombre, String descripcionRol) {
        // Usar requireNonNull para asegurar que los parámetros no sean nulos
        this.nombre = Objects.requireNonNull(nombre, "El nombre del rol no puede ser nulo");
        this.descripcionRol = Objects.requireNonNull(descripcionRol, "La descripción del rol no puede ser nula");
    }

    // Métodos Getters

    public String getNombreRol() {
        return nombre;
    }

    public String getDescripcionRol() {
        return descripcionRol;
    }

    // Métodos Setters

    public void setNombreRol(String nombre) {
        // Usar requireNonNull en el setter para validar la entrada
        this.nombre = Objects.requireNonNull(nombre, "El nombre del rol no puede ser nulo");
    }

    public void setDescripcionRol(String descripcionRol) {
        // Usar requireNonNull en el setter para validar la entrada
        this.descripcionRol = Objects.requireNonNull(descripcionRol, "La descripción del rol no puede ser nula");
    }

    // Métodos de comportamiento

    public boolean esResponsableDeReparacion() { // Corregido el tipo de retorno a boolean
        return nombre.equals("Responsable de Reparación");
    }
}