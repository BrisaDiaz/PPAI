package com.mycompany.ppai.repositories;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.ppai.entities.Sismografo;

public class SismografoRepository {
    private static final List<Sismografo> sismografos = new ArrayList<>();
        
    public static List<Sismografo> obtenerTodos() {
        return sismografos;
    }

    public static void guardar(Sismografo sismografo) {
        sismografos.add(sismografo);
    }
    
    public static void guardarTodos(List<Sismografo> listaSismografos) {
        sismografos.addAll(listaSismografos);
    }
}
