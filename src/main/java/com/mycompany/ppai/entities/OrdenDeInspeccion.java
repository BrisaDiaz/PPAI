package com.mycompany.ppai.entities;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.List;
import com.google.gson.JsonObject; 

public class OrdenDeInspeccion {

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFinalizacion;
    private LocalDateTime fechaHoraCierre;
    private Integer numeroOrden;
    private String observacionCierre;
    private Estado estado;
    private Empleado empleado;
    private EstacionSismologica estacionSismologica;

    // Constructor
    public OrdenDeInspeccion(LocalDateTime fechaHoraInicio, Integer numeroOrden, Estado estado, Empleado empleado, EstacionSismologica estacionSismologica) {
        this.fechaHoraInicio = Objects.requireNonNull(fechaHoraInicio, "La fecha y hora de inicio no pueden ser nulas");
        this.numeroOrden = Objects.requireNonNull(numeroOrden, "El número de orden no puede ser nulo");
        this.estado = Objects.requireNonNull(estado, "El estado no puede ser nulo");
        this.empleado = Objects.requireNonNull(empleado, "El empleado no puede ser nulo");
        this.estacionSismologica = Objects.requireNonNull(estacionSismologica, "La estación sismológica no puede ser nula");
    }

    // Métodos Getters

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFinalizacion() {
        return fechaHoraFinalizacion;
    }

    public LocalDateTime getFechaHoraCierre() {
        return fechaHoraCierre;
    }

    public Integer getNumeroOrden() {
        return numeroOrden;
    }

    public String getObservacionCierre() {
        return observacionCierre;
    }

    public Estado getEstado() {
        return estado;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public EstacionSismologica getEstacionSismologica() {
        return estacionSismologica;
    }

    // Métodos Setters

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = Objects.requireNonNull(fechaHoraInicio, "La fecha y hora de inicio no pueden ser nulas");
    }

    public void setFechaHoraFinalizacion(LocalDateTime fechaHoraFinalizacion) {
        this.fechaHoraFinalizacion = fechaHoraFinalizacion;
    }

    public void setFechaHoraCierre(LocalDateTime fechaHoraCierre) {
        this.fechaHoraCierre = fechaHoraCierre;
    }

    public void setNumeroOrden(Integer numeroOrden) {
        this.numeroOrden = Objects.requireNonNull(numeroOrden, "El número de orden no puede ser nulo");
    }

    public void setObservacionCierre(String observacionCierre) {
        this.observacionCierre = observacionCierre;
    }

    public void setEstado(Estado estado) {
        this.estado = Objects.requireNonNull(estado, "El estado no puede ser nulo");
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = Objects.requireNonNull(empleado, "El empleado no puede ser nulo");
    }

    public void setEstacionSismologica(EstacionSismologica estacionSismologica) {
        this.estacionSismologica = Objects.requireNonNull(estacionSismologica, "La estación sismológica no puede ser nula");
    }

    // Métodos de comportamiento

    public boolean esMiRI(Empleado empleado) {
        return this.empleado.equals(empleado);
    }

    public boolean estoyCompletamenteRealizada() {
        return this.estado.esCompletamenteRealizada();
    }

    public JsonObject mostrarDatosOrdeneDeInspeccion(List<Sismografo> sismografos) { 
        JsonObject datos = new JsonObject();
        datos.addProperty("numeroOrden", this.getNumeroOrden());
        datos.addProperty("fechaHoraFinalizacion", this.getFechaHoraFinalizacion().toString());
        datos.addProperty("nombreEstacion", this.estacionSismologica.getNombre());
        datos.addProperty("identificadorSismografo", this.estacionSismologica.mostrarIdentificadorSismografo(sismografos));

        return datos;
    }

    public void cerrar(Estado estadoCerrada, String observacionCierre, LocalDateTime fechaHoraCierre) {
        this.setEstado(estadoCerrada);
        this.setObservacionCierre(observacionCierre);
        this.setFechaHoraCierre(fechaHoraCierre);
    }

    public void actualizarSismografoFueraServicio(LocalDateTime fechaHoraActual, Empleado responsableDeInspeccion,
    Estado estadoFueraServicio, List<Object[]> motivosFueraServicio,  List<Sismografo> sismografos) {
        this.estacionSismologica.actualizarSismografoFueraServicio(fechaHoraActual, responsableDeInspeccion, estadoFueraServicio, motivosFueraServicio, sismografos);

    }

    public void actualizarSismografoOnline(LocalDateTime fechaHoraActual, Empleado responsableDeInspeccion,
    Estado estadoOnline, List<Sismografo> sismografos) {
        this.estacionSismologica.actualizarSismografoOnline(fechaHoraActual, responsableDeInspeccion, estadoOnline, sismografos);
    }
}