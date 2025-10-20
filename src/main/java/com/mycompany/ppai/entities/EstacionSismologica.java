package com.mycompany.ppai.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class EstacionSismologica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false)
    private String nombre;

    @Column(nullable=false)
    private String codigoEstacion;

    @Column(nullable=false)
    private String documentoCertificacionAdq;

    @Column(nullable=false)
    private LocalDateTime fechaSolicitudCertificacion;

    @Column(nullable=false)
    private Float latitud;

    @Column(nullable=false)
    private Float longitud;

    @Column(nullable=false)
    private Integer nroCertificacionAdquisicion;


    // Métodos de comportamiento

    public String obtenerIdentificadorSismografo(List<Sismografo> sismografos) {
        for (Sismografo sismografo : sismografos) {
            if (sismografo.esMiEstacion(this)) {
                return sismografo.getIdentificadorSismografo();
            }
        }
        return null;
    }

    public void actualizarSismografoFueraServicio(LocalDateTime fechaHoraActual, Empleado responsableDeInspeccion,
    Estado estadoFueraServicio, List<MotivoTipo> motivosFueraServicio, List<String> comentariosFueraServicio, List<Sismografo> sismografos) {

        for (Sismografo sismografo : sismografos) {
            if (sismografo.esMiEstacion(this)) {
                sismografo.retirarDeServicio(fechaHoraActual, responsableDeInspeccion,estadoFueraServicio, motivosFueraServicio, comentariosFueraServicio);
                break;
            }
        }

    }

    public void actualizarSismografoOnline(LocalDateTime fechaHoraActual, Empleado responsableDeInspeccion,
    Estado estadoOnline, List<Sismografo> sismografos) {

        for (Sismografo sismografo : sismografos) {
            if (sismografo.esMiEstacion(this)) {
                sismografo.ponerOnline(fechaHoraActual, responsableDeInspeccion, estadoOnline);
                break;
            }
        }
    }
}