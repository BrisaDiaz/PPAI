package com.mycompany.ppai.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false)
    private NombreRolEnum nombre;

    @Column(nullable=false)
    private String descripcionRol;

    // Métodos de comportamiento

    public boolean esResponsableDeReparacion() {
        return nombre.equals(NombreRolEnum.RESPONSABLE_DE_REPARACION);
    }
    public boolean esResponsableDeInspeccion() {
        return nombre.equals(NombreRolEnum.RESPONSABLE_DE_INSPECCION);
    }
}