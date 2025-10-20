package com.mycompany.ppai.entities;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.List;
import com.google.gson.JsonObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class OrdenDeInspeccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false)
    private LocalDateTime fechaHoraInicio;

    @Column(nullable=false)
    private Integer numeroOrden;

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable=false)
    private Estado estadoActual;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable=false)
    private Empleado empleado;

    @ManyToOne
    @JoinColumn(name = "estacion_sismologica_id", nullable=false)
    private EstacionSismologica estacionSismologica;

    private LocalDateTime fechaHoraFinalizacion;

    private LocalDateTime fechaHoraCierre;

    private String observacionCierre;

    // Constructor
    public OrdenDeInspeccion(LocalDateTime fechaHoraInicio, Integer numeroOrden, Estado estado, Empleado empleado,
            EstacionSismologica estacionSismologica) {

        this.fechaHoraInicio = fechaHoraInicio;
        this.numeroOrden = numeroOrden;
        this.estadoActual = estado;
        this.empleado = empleado;
        this.estacionSismologica = estacionSismologica;
    }

    // Métodos de comportamiento

    public boolean esMiRI(Empleado empleado) {
        return this.empleado.getId().equals(empleado.getId());
    }

    public boolean estoyCompletamenteRealizada() {
        return this.estadoActual.esCompletamenteRealizada();
    }

    public JsonObject mostrarDatosOrdeneDeInspeccion(List<Sismografo> sismografos) {
        JsonObject datos = new JsonObject();
        datos.addProperty("numeroOrden", this.getNumeroOrden());
        datos.addProperty("fechaHoraFinalizacion", this.getFechaHoraFinalizacion().toString());
        datos.addProperty("nombreEstacion", this.estacionSismologica.getNombre());
        datos.addProperty("identificadorSismografo",
        this.estacionSismologica.obtenerIdentificadorSismografo(sismografos));

        return datos;
    }
    public String obtenerIdentificadorSismografo(List<Sismografo> sismografos) {
       return this.estacionSismologica.obtenerIdentificadorSismografo(sismografos);
    }

    public void cerrar(Estado estadoCerrada, String observacionCierre, LocalDateTime fechaHoraCierre) {
        this.setEstadoActual(estadoCerrada);
        this.setObservacionCierre(observacionCierre);
        this.setFechaHoraCierre(fechaHoraCierre);
    }

    public void actualizarSismografoFueraServicio(LocalDateTime fechaHoraActual, Empleado responsableDeInspeccion,
    Estado estadoFueraServicio, List<MotivoTipo> motivosFueraServicio,  List<String> comentariosFueraServicio,  List<Sismografo> sismografos) {
        this.estacionSismologica.actualizarSismografoFueraServicio(fechaHoraActual, responsableDeInspeccion, estadoFueraServicio, motivosFueraServicio, comentariosFueraServicio, sismografos);

    }

    public void actualizarSismografoOnline(LocalDateTime fechaHoraActual, Empleado responsableDeInspeccion,
    Estado estadoOnline, List<Sismografo> sismografos) {
        this.estacionSismologica.actualizarSismografoOnline(fechaHoraActual, responsableDeInspeccion, estadoOnline, sismografos);
    }
}