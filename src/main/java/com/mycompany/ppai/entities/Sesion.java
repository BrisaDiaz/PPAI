
package com.mycompany.ppai.entities;

import java.time.LocalDateTime;
import java.util.Objects; // Importar Objects para usar requireNonNull

public class Sesion {
    private LocalDateTime fechaHoraDesde;
    private LocalDateTime fechaHoraHasta;
    private Usuario usuario;

    public Sesion(LocalDateTime fechaHoraDesde, Usuario usuario) {
        // Usar requireNonNull para asegurar que los parámetros no sean nulos
        this.fechaHoraDesde = Objects.requireNonNull(fechaHoraDesde, "La fecha y hora de inicio no pueden ser nulas");
        this.usuario = Objects.requireNonNull(usuario, "El usuario no puede ser nulo");
    }

    // Métodos getters
    public LocalDateTime getFechaHoraDesde() {
        return fechaHoraDesde;
    }

    public LocalDateTime getFechaHoraHasta() {
        return fechaHoraHasta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    // Métodos setters
    public void setFechaHoraDesde(LocalDateTime fechaHoraDesde) {
        // Usar requireNonNull en el setter para validar la entrada
        this.fechaHoraDesde = Objects.requireNonNull(fechaHoraDesde, "La fecha y hora de inicio no pueden ser nulas");
    }

    public void setFechaHoraHasta(LocalDateTime fechaHoraHasta) {
        this.fechaHoraHasta = fechaHoraHasta; // No se valida si es nulo, se asume que puede serlo
    }

    public void setUsuario(Usuario usuario) {
        // Usar requireNonNull en el setter para validar la entrada
        this.usuario = Objects.requireNonNull(usuario, "El usuario no puede ser nulo");
    }

    // Métodos de comportamiento
    public Empleado obtenerRILogeado() {
        return this.usuario.getRILogeado();
    }
}