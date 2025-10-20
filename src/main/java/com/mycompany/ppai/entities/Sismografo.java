package com.mycompany.ppai.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Sismografo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false)
    private LocalDateTime fechaAdquisicion;

    @Column(nullable=false, unique=true)
    private String identificadorSismografo;

    @Column(nullable=false)
    private Integer nroSerie;

    @OneToOne
    @JoinColumn(name = "estacion_sismologica_id", nullable=false)
    private EstacionSismologica estacionSismologica;

    @OneToMany
    @JoinColumn(name = "sismografo_id")
    private List<CambioEstado> cambioEstado;

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable=false)
    private Estado estadoActual;

    public Sismografo(LocalDateTime fechaAdquisicion, String identificadorSismografo, Integer nroSerie,
    EstacionSismologica estacionSismologica, Estado estadoInicial, LocalDateTime fechaHoraActual, Empleado responsableDeInspeccion) {

        this.fechaAdquisicion = fechaAdquisicion;
        this.identificadorSismografo = identificadorSismografo;
        this.nroSerie = nroSerie;
        this.estacionSismologica = estacionSismologica;
        this.estadoActual = estadoInicial;
        this.cambioEstado = new ArrayList<>();
        CambioEstado cambioEstadoInicial = new CambioEstado(estadoInicial, fechaHoraActual, responsableDeInspeccion,null, null);
        this.cambioEstado.add(cambioEstadoInicial);
    }

    // Métodos de comportamiento

    public boolean esMiEstacion(EstacionSismologica estacion) {
        return this.estacionSismologica.getId().equals(estacion.getId());
    }

    public void retirarDeServicio(LocalDateTime fechaHoraActual, Empleado responsableDeInspeccion,
        Estado estadoFueraServicio, List<MotivoTipo> motivosFueraServicio, List<String> comentariosFueraServicio) {

        this.finalizarCambioEstadoActual(fechaHoraActual);
        this.crearCambioEstado(estadoFueraServicio, fechaHoraActual, responsableDeInspeccion, motivosFueraServicio, comentariosFueraServicio);
    }

    public void ponerOnline(LocalDateTime fechaHoraActual, Empleado responsableDeInspeccion, Estado estadoOnline) {

        this.finalizarCambioEstadoActual(fechaHoraActual);
        this.crearCambioEstado(estadoOnline, fechaHoraActual, responsableDeInspeccion, new ArrayList<>(), new ArrayList<>()); // Pasamos null para motivos
    }

    public void finalizarCambioEstadoActual(LocalDateTime fechaHoraFin) {
        CambioEstado cambioEstadoActual = null;

        for (CambioEstado cambioEstadoIterado : this.getCambioEstado()) {
            if (cambioEstadoIterado.getFechaHoraFin() == null) {
                cambioEstadoActual = cambioEstadoIterado;
                break;
            }
        }

        if (cambioEstadoActual == null) {
            throw new IllegalStateException("No se encontró un cambio de estado actual sin fecha de fin.");
        }

        cambioEstadoActual.setFechaHoraFin(fechaHoraFin);
    }

    public void addCambioEstado(CambioEstado cambioEstado) {
        this.cambioEstado.add(cambioEstado);
    }

    public void crearCambioEstado(Estado nuevoEstado, LocalDateTime fechaHoraActual,
            Empleado responsableDeInspeccion, List<MotivoTipo> motivosFueraServicio, List<String> comentariosFueraServicio) {

        // Si es el estado inicial o si el nuevo estado es diferente al actual
        if (nuevoEstado != null && (this.estadoActual == null || !nuevoEstado.equals(this.estadoActual))) {
            CambioEstado nuevoCambioEstado = new CambioEstado(nuevoEstado, fechaHoraActual, responsableDeInspeccion,
                    motivosFueraServicio, comentariosFueraServicio);
            addCambioEstado(nuevoCambioEstado);
            this.setEstadoActual(nuevoEstado);
        }
    }
}