package com.mycompany.ppai.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false)
    private String nombreUsuario;

    @Column(nullable=false)
    private String constraseña;

    @OneToOne
    @JoinColumn(name = "empleado_id", nullable=false)
    private Empleado empleado;

    // Métodos de comportamiento

    public Empleado getRILogeado() {
        return this.getEmpleado();
    }
}