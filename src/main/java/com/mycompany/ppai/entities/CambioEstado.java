package com.mycompany.ppai.entities;
 

 import java.time.LocalDateTime;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
public class CambioEstado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false)
    private LocalDateTime fechaHoraInicio;

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable=false)
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "responsable_id", nullable=false)
    private Empleado responsableDeInspeccion;

    @ManyToOne
    @JoinColumn(name = "sismografo_id", nullable=false)
    private Sismografo sismografo;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
        name = "cambio_estado_motivo",
        joinColumns = @JoinColumn(name = "cambio_estado_id"),
        inverseJoinColumns = @JoinColumn(name = "motivo_fuera_servicio_id")
    )
    private List<MotivoFueraServicio> motivoFueraServicio;

    private LocalDateTime fechaHoraFin;

    // Constructor para un CambioEstado general.
    public CambioEstado(Estado estado, LocalDateTime fechaHoraInicio, Empleado responsableDeInspeccion, Sismografo sismografo) {
      this.estado = estado;
      this.fechaHoraInicio = fechaHoraInicio;
      this.responsableDeInspeccion = responsableDeInspeccion;
      this.sismografo = sismografo;
    }

    // Constructor para un CambioEstado cuando el sismógrafo se pone Fuera de Servicio.
    public CambioEstado(Estado estado, LocalDateTime fechaHoraInicio, Empleado responsableDeInspeccion, Sismografo sismografo, List<Object[]> motivosFueraServicio) {
      this(estado, fechaHoraInicio, responsableDeInspeccion, sismografo); // Llamar al constructor general
      this.crearMotivosFueraDeServicio(motivosFueraServicio);
    }


    // Métodos de comportamiento

    public void crearMotivosFueraDeServicio(List<Object[]> motivosFueraServicio) {
      this.motivoFueraServicio = new ArrayList<>();

      if (motivosFueraServicio != null) {

        for (Object[] motivoData : motivosFueraServicio) {
          MotivoTipo tipo = (MotivoTipo) motivoData[0];
          String comentario = (String) motivoData[1];

          MotivoFueraServicio motivo = new MotivoFueraServicio(null, comentario, tipo);
          this.motivoFueraServicio.add(motivo);
        }
      }
    }

    public boolean esCambioEstadoActual() {
      return this.fechaHoraFin == null;
    }
}