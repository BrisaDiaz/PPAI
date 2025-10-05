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
public class Estado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false)
    private AmbitoEstadoEnum ambito;

    @Column(nullable=false)
    private NombreEstadoEnum nombreEstado;

    // Métodos de comportamiento

    public boolean esAmbitoOrdenDeInspeccion() {
        return ambito.equals(AmbitoEstadoEnum.ORDEN_DE_INSPECCION);
    }

    public boolean esAmbitoSismografo() {
        return ambito.equals(AmbitoEstadoEnum.SISMOGRAFO);
    }

    public boolean esCompletamenteRealizada() {
        return nombreEstado.equals(NombreEstadoEnum.COMPLETAMENTE_REALIZADA);
    }

    public boolean esCerrada() {
        return nombreEstado.equals(NombreEstadoEnum.CERRADA);
    }

    public boolean esFueraDeServicio() {
        return nombreEstado.equals(NombreEstadoEnum.FUERA_DE_SERVICIO);
    }

    public boolean esOnline() {
        return nombreEstado.equals(NombreEstadoEnum.ONLINE);
    }
}