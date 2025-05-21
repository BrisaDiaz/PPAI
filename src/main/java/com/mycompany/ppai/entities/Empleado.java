package com.mycompany.ppai.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Empleado {
    private String nombre;
    private String apellido;
    private String telefono;
    private String mail;
    private Rol rol;

    // Simulación de persistencia
    private static final List<Empleado> todosLosEmpleados = new ArrayList<>();

    public Empleado(String nombre, String apellido, String telefono, String mail, Rol rol) {
        this.nombre = Objects.requireNonNull(nombre, "Nombre no puede ser nulo");
        this.apellido = Objects.requireNonNull(apellido, "Apellido no puede ser nulo");
        this.telefono = Objects.requireNonNull(telefono, "Teléfono no puede ser nulo");
        this.mail = Objects.requireNonNull(mail, "mail no puede ser nulo");
        this.rol = Objects.requireNonNull(rol, "Rol no puede ser nulo");
    }

    // Getters

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public String obtenerMail() {
        return mail;
    }

    public Rol getRol() {
        return rol;
    }

    // Setters

    public void setNombre(String nombre) {
        this.nombre = Objects.requireNonNull(nombre, "Nombre no puede ser nulo");
    }

    public void setApellido(String apellido) {
        this.apellido = Objects.requireNonNull(apellido, "Apellido no puede ser nulo");
    }

    public void setTelefono(String telefono) {
        this.telefono = Objects.requireNonNull(telefono, "Teléfono no puede ser nulo");
    }

    public void setMail(String mail) {
        this.mail = Objects.requireNonNull(mail, "mail no puede ser nulo");
    }

    public void setRol(Rol rol) {
        this.rol = Objects.requireNonNull(rol, "Rol no puede ser nulo");
    }

    // Métodos de comportamiento

    public boolean esResponsableDeReparacion() {
        return rol.esResponsableDeReparacion();
    }

    // simulación de persistencia
    public static List<Empleado> obtenerTodosLosEmpleados() {
        return todosLosEmpleados;
    }
    
}