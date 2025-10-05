
package com.mycompany.ppai.entities;

import java.time.LocalDateTime;
import java.util.Objects;

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
public class Sesion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false)
    private LocalDateTime fechaHoraDesde;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable=false)
    private Usuario usuario;

    private LocalDateTime fechaHoraHasta;

    // Métodos de comportamiento
    public Empleado obtenerRILogeado() {
        return this.usuario.getRILogeado();
    }
}