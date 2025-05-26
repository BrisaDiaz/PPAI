package com.mycompany.ppai.repositories;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.ppai.entities.OrdenDeInspeccion;

public class OrdenDeInspeccionRepository {
    private static final List<OrdenDeInspeccion> ordenesDeInspeccion = new ArrayList<>();
        
    public static List<OrdenDeInspeccion> obtenerTodos() {
        return ordenesDeInspeccion;
    }

    public static void guardar(OrdenDeInspeccion ordenDeInspeccion) {
        ordenesDeInspeccion.add(ordenDeInspeccion);
    }
    
    public static void guardarTodos(List<OrdenDeInspeccion> listaOrdenDeInspeccions) {
        ordenesDeInspeccion.addAll(listaOrdenDeInspeccions);
    }

    public static OrdenDeInspeccion obtenerPorNumero(Integer numeroOrden) {
        for (OrdenDeInspeccion orden : ordenesDeInspeccion) {
            if (orden.getNumeroOrden().equals(numeroOrden)) {
                return orden;
            }
        }
        return null;
    }
}
