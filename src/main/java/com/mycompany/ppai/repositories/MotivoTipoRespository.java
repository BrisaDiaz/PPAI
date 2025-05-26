package com.mycompany.ppai.repositories;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.ppai.entities.MotivoTipo;

public class MotivoTipoRespository {
    private static final List<MotivoTipo> motivosTipo = new ArrayList<>();
        
    public static List<MotivoTipo> obtenerTodos() {
        return motivosTipo;
    }

    public static void guardar(MotivoTipo motivoTipo) {
        motivosTipo.add(motivoTipo);
    }
    
    public static void guardarTodos(List<MotivoTipo> listaMotivoTipos) {
        motivosTipo.addAll(listaMotivoTipos);
    }

    public static MotivoTipo obtenerPorDescripcion(String descripcion) {
        for (MotivoTipo motivoTipo : motivosTipo) {
            if (motivoTipo.getDescripcion().equalsIgnoreCase(descripcion)) {
                return motivoTipo;
            }
        }
        return null;
    }
}
