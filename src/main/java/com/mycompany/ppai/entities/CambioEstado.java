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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
        name = "cambio_estado_motivo",
        joinColumns = @JoinColumn(name = "cambio_estado_id"),
        inverseJoinColumns = @JoinColumn(name = "motivo_fuera_servicio_id")
    )
    private List<MotivoFueraServicio> motivoFueraServicio;

    private LocalDateTime fechaHoraFin;

    // Constructor para un CambioEstado general.
    public CambioEstado(Estado estado, LocalDateTime fechaHoraInicio, Empleado responsableDeInspeccion) {
      this.estado = estado;
      this.fechaHoraInicio = fechaHoraInicio;
      this.responsableDeInspeccion = responsableDeInspeccion;
    }

    // Constructor para un CambioEstado cuando el sismógrafo se pone Fuera de Servicio.
    public CambioEstado(Estado estado, LocalDateTime fechaHoraInicio, Empleado responsableDeInspeccion,
        List<MotivoTipo> motivosFueraServicio, List<String> comentariosFueraServicio) {
      this(estado, fechaHoraInicio, responsableDeInspeccion); // Llamar al constructor general
      this.crearMotivosFueraDeServicio(motivosFueraServicio, comentariosFueraServicio);
    }


    // Métodos de comportamiento

    public void crearMotivosFueraDeServicio(List<MotivoTipo> motivosFueraServicio, List<String> comentariosFueraServicio) {
      this.motivoFueraServicio = new ArrayList<>();

      if (motivosFueraServicio != null) {

        for (int i = 0; i < motivosFueraServicio.size(); i++) {
          MotivoTipo tipo = motivosFueraServicio.get(i);
          String comentario = comentariosFueraServicio.get(i);
          MotivoFueraServicio motivo = new MotivoFueraServicio(null, comentario, tipo);
          this.motivoFueraServicio.add(motivo);
        }
      }
    }

    public boolean esCambioEstadoActual() {
      return this.fechaHoraFin == null;
    }
}