package com.mycompany.ppai.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects; // Importar Objects for requireNonNull

public class EstacionSismologica {

    private String nombre;
    private String codigoEstacion;
    private String documentoCertificacionAdq;
    private LocalDateTime fechaSolicitudCertificacion;
    private Float latitud;
    private Float longitud;
    private Integer nroCertificacionAdquisicion;

    // Constructor
    
    public EstacionSismologica(String nombre, String codigoEstacion, String documentoCertificacionAdq,
                            LocalDateTime fechaSolicitudCertificacion, Float latitud, Float longitud, Integer nroCertificacionAdquisicion) {
        // Use requireNonNull to validate constructor parameters
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.codigoEstacion = Objects.requireNonNull(codigoEstacion, "El código de estación no puede ser nulo");
        this.documentoCertificacionAdq = Objects.requireNonNull(documentoCertificacionAdq, "El documento de certificación no puede ser nulo");
        this.fechaSolicitudCertificacion = Objects.requireNonNull(fechaSolicitudCertificacion, "La fecha de solicitud no puede ser nula");
        this.latitud = Objects.requireNonNull(latitud, "La latitud no puede ser nula");
        this.longitud = Objects.requireNonNull(longitud, "La longitud no puede ser nula");
        this.nroCertificacionAdquisicion = Objects.requireNonNull(nroCertificacionAdquisicion, "El número de certificación no puede ser nulo");
    }

    // Getters

    public String getNombre() {
        return nombre;
    }

    public String getCodigoEstacion() {
        return codigoEstacion;
    }

    public String getDocumentoCertificacionAdq() {
        return documentoCertificacionAdq;
    }

    public LocalDateTime getFechaSolicitudCertificacion() {
        return fechaSolicitudCertificacion;
    }

    public Float getLatitud() {
        return latitud;
    }

    public Float getLongitud() {
        return longitud;
    }

    public Integer getNroCertificacionAdquisicion() {
        return nroCertificacionAdquisicion;
    }

    // Setters

    public void setNombre(String nombre) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
    }

    public void setCodigoEstacion(String codigoEstacion) {
        this.codigoEstacion = Objects.requireNonNull(codigoEstacion, "El código de estación no puede ser nulo");
    }

    public void setDocumentoCertificacionAdq(String documentoCertificacionAdq) {
        this.documentoCertificacionAdq = Objects.requireNonNull(documentoCertificacionAdq, "El documento de certificación no puede ser nulo");
    }

    public void setFechaSolicitudCertificacion(LocalDateTime fechaSolicitudCertificacion) {
        this.fechaSolicitudCertificacion = Objects.requireNonNull(fechaSolicitudCertificacion, "La fecha de solicitud no puede ser nula");
    }

    public void setLatitud(Float latitud) {
        this.latitud = Objects.requireNonNull(latitud, "La latitud no puede ser nula");
    }

    public void setLongitud(Float longitud) {
        this.longitud = Objects.requireNonNull(longitud, "La longitud no puede ser nula");
    }

    public void setNroCertificacionAdquisicion(Integer nroCertificacionAdquisicion) {
        this.nroCertificacionAdquisicion = Objects.requireNonNull(nroCertificacionAdquisicion, "El número de certificación no puede ser nulo");
    }

    // Métodos de comportamiento

    public String mostrarIdentificadorSismografo(List<Sismografo> sismografos) {
        for (Sismografo sismografo : sismografos) {
            if (sismografo.esMiEstacion(this)) {
                return sismografo.getIdentificador();
            }
        }
        return null;
    }

    public void actualizarSismografoFueraServicio(LocalDateTime fechaHoraActual, Empleado responsableDeInspeccion,
    Estado estadoFueraServicio, List<Object[]> motivosFueraServicio, List<Sismografo> sismografos) {

        for (Sismografo sismografo : sismografos) {
            if (sismografo.esMiEstacion(this)) {
                sismografo.retirarDeServicio(fechaHoraActual, responsableDeInspeccion,estadoFueraServicio, motivosFueraServicio);
                
                break;
            }
        }

    }

    public void actualizarSismografoOnline(LocalDateTime fechaHoraActual, Empleado responsableDeInspeccion,
    Estado estadoOnline, List<Sismografo> sismografos) {

        for (Sismografo sismografo : sismografos) {
            if (sismografo.esMiEstacion(this)) {
                sismografo.ponerOnline(fechaHoraActual, responsableDeInspeccion, estadoOnline);
                
                break;
            }
        }
    }
}