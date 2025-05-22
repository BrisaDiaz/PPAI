package com.mycompany.ppai.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // Importar Objects para usar requireNonNull

public class Estado {
    private String ambito;
    private String nombreEstado;

    // Simulación de persistencia
    private static final List<Estado> todosLosEstados = new ArrayList<>();

    // Constructor
    public Estado(String nombreEstado, String ambito) {
        // Usar requireNonNull para asegurar que los parámetros no sean nulos
        this.ambito = Objects.requireNonNull(ambito, "El ámbito no puede ser nulo");
        this.nombreEstado = Objects.requireNonNull(nombreEstado, "El nombre del estado no puede ser nulo");
    }

    // Métodos Getters

    public String getAmbito() {
        return ambito;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    // Métodos Setters

    public void setAmbito(String ambito) {
        // Usar requireNonNull en el setter para validar la entrada
        this.ambito = Objects.requireNonNull(ambito, "El ámbito no puede ser nulo");
    }

    public void setNombreEstado(String nombreEstado) {
        // Usar requireNonNull en el setter para validar la entrada
        this.nombreEstado = Objects.requireNonNull(nombreEstado, "El nombre del estado no puede ser nulo");
    }

    // Métodos de comportamiento


    public boolean esAmbitoOrdenDeInspeccion() {
        return ambito.equals("Orden de Inspección");
    }

    public boolean esAmbitoSismografo() {
        return ambito.equals("Sismógrafo");
    }

    public boolean esCompletamenteRealizada() {
        return nombreEstado.equals("Completamente Realizada");
    }

    public boolean esCerrada() {
        return nombreEstado.equals("Cerrada");
    }
    public boolean esFueraDeServicio() {
        return nombreEstado.equals("Fuera de Servicio");
    }
    public boolean esOnline() {
        return nombreEstado.equals("Online");
    }

    // Simulación de persistencia
    public static List<Estado> obtenerTodosLosEstados() {
        return todosLosEstados;
    }
    public static void agregarEstado(Estado estado) {
        todosLosEstados.add(Objects.requireNonNull(estado, "El estado no puede ser nulo"));
    }
}