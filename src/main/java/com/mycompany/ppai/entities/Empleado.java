package com.mycompany.ppai.entities;

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
@Setter
@Getter
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false)
    private String nombre;

    @Column(nullable=false)
    private String apellido;

    @Column(nullable=false)
    private String telefono;

    @Column(nullable=false)
    private String mail;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable=false)
    private Rol rol;

    // Métodos de comportamiento

    public boolean esResponsableDeReparacion() {
        return rol.esResponsableDeReparacion();
    }
}