package com.mycompany.ppai.entities;

import java.util.Objects; // Importar Objects para usar requireNonNull

public class Usuario {
    private String nombreUsuario;
    private String constraseña;
    private Empleado empleado;

    // Constructor
    public Usuario(String nombreUsuario, String constraseña, Empleado empleado) {
        // Usar requireNonNull para asegurar que los parámetros no sean nulos
        this.nombreUsuario = Objects.requireNonNull(nombreUsuario, "El nombre de usuario no puede ser nulo");
        this.constraseña = Objects.requireNonNull(constraseña, "La contraseña no puede ser nula");
        this.empleado = Objects.requireNonNull(empleado, "El empleado no puede ser nulo");
    }

    // Métodos getters

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getConstraseña() {
        return constraseña;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    // Métodos setters

    public void setNombreUsuario(String nombreUsuario) {
        // Usar requireNonNull en el setter para validar la entrada
        this.nombreUsuario = Objects.requireNonNull(nombreUsuario, "El nombre de usuario no puede ser nulo");
    }

    public void setConstraseña(String constraseña) {
        // Usar requireNonNull en el setter para validar la entrada
        this.constraseña = Objects.requireNonNull(constraseña, "La contraseña no puede ser nula");
    }

    public void setEmpleado(Empleado empleado) {
        // Usar requireNonNull en el setter para validar la entrada
        this.empleado = Objects.requireNonNull(empleado, "El empleado no puede ser nulo");
    }

    // Métodos de comportamiento

    public Empleado getRILogeado() {
        return empleado;
    }
}