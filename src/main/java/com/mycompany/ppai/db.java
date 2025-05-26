package com.mycompany.ppai;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.ppai.entities.*;
import com.mycompany.ppai.boundaries.*;


public class db {
    private static final List<Estado> estados = new ArrayList<>();
    private static final List<MotivoTipo> motivosTipo = new ArrayList<>();
    private static final List<Empleado> empleados = new ArrayList<>();
    private static final List<OrdenDeInspeccion> ordenesDeInspeccion = new ArrayList<>();
    private static final List<Sismografo> sismografos = new ArrayList<>();
    private static final List<MonitorCCRS> monitoresCCRS = new ArrayList<>();


    public static List<Empleado> obtenerEmpleados() {
        return empleados;
    }

    public static List<Estado> obtenerEstados() {
        return estados;
    }

    public static List<MotivoTipo> obtenerMotivosTipo() {
        return motivosTipo;
    }

    public static List<OrdenDeInspeccion> obtenerOrdenesDeInspeccion() {
        return ordenesDeInspeccion;
    }

    public static List<Sismografo> obtenerSismografos() {
        return sismografos;
    }

    public static List<MonitorCCRS> obtenerMonitoresCCRS() {
        return monitoresCCRS;
    }
    
    public static void agregarEstado(Estado estado) {
        estados.add(estado);
    }

    public static void agregarMotivoTipo(MotivoTipo motivoTipo) {
        motivosTipo.add(motivoTipo);
    }

    public static void agregarEmpleado(Empleado empleado) {
        empleados.add(empleado);
    }

    public static void agregarOrdenDeInspeccion(OrdenDeInspeccion orden) {
        ordenesDeInspeccion.add(orden);
    }

    public static void agregarSismografo(Sismografo sismografo) {
        sismografos.add(sismografo);
    }

    public static void agregarMonitorCCRS(MonitorCCRS monitor) {
        monitoresCCRS.add(monitor);
    }
   
    
    public static OrdenDeInspeccion obtenerOrdenPorNumero(Integer numeroOrden) {
        for (OrdenDeInspeccion orden : ordenesDeInspeccion) {
            if (orden.getNumeroOrden().equals(numeroOrden)) {
                return orden;
            }
        }
        return null;
    }

    public static MotivoTipo obtenerMotivoTipoPorDescripcion(String descripcion) {
        for (MotivoTipo motivoTipo : motivosTipo) {
            if (motivoTipo.getDescripcion().equalsIgnoreCase(descripcion)) {
                return motivoTipo;
            }
        }
        return null; 
    }
}