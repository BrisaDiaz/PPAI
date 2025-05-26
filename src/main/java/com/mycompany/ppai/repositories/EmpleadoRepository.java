package com.mycompany.ppai.repositories;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.ppai.entities.Empleado;

public class EmpleadoRepository {
    private static final List<Empleado> empleados = new ArrayList<>();
        
    public static List<Empleado> obtenerTodos() {
        return empleados;
    }

    public static void guardar(Empleado empleado) {
        empleados.add(empleado);
    }
    
    public static void guardarTodos(List<Empleado> listaEmpleados) {
        empleados.addAll(listaEmpleados);
    }
}
