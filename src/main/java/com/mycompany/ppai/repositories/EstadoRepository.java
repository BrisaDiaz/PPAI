package com.mycompany.ppai.repositories;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.ppai.entities.Estado;

public class EstadoRepository {
    private static final List<Estado> estados = new ArrayList<>();
        
    public static List<Estado> obtenerTodos() {
        return estados;
    }

    public static void guardar(Estado estado) {
        estados.add(estado);
    }
    
    public static void guardarTodos(List<Estado> listaEstados) {
        estados.addAll(listaEstados);
    }
}
