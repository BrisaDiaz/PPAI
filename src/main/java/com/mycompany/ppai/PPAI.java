package com.mycompany.ppai;

import com.mycompany.ppai.boundaries.InterfazNotificacion;
import com.mycompany.ppai.boundaries.MonitorCCRS;
import com.mycompany.ppai.boundaries.PantallaCierreOrdenInspeccion;
import com.mycompany.ppai.controllers.GestorCierreOrdenInspeccion;
import com.mycompany.ppai.entities.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PPAI {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            // Crear roles
            Rol rolRI = new Rol("Responsable de Inspección", "Realiza inspecciones");
            Rol rolReparacion = new Rol("Responsable de Reparación", "Repara sismógrafos");

            // Crear empleados
            Empleado empleadoRI = new Empleado("Juan", "Pérez", "123456789", "juan.perez@example.com", rolRI);
            Empleado empleadoReparacion1 = new Empleado("Ana", "Gómez", "987654321", "ana.gomez@example.com", rolReparacion);
            Empleado.obtenerTodosLosEmpleados().addAll(List.of(empleadoRI, empleadoReparacion1));

            // Crear usuarios
            Usuario usuarioRI = new Usuario("jperez", "password", empleadoRI);

            // Crear sesión
            Sesion sesion = new Sesion(LocalDateTime.now().minusHours(1), usuarioRI);

            // Crear estados
            Estado estadoCompletamenteRealizada = new Estado("Completamente Realizada", "Orden de Inspección");
            Estado estadoCerradaOrden = new Estado("Cerrada", "Orden de Inspección");
            Estado estadoFueraDeServicioSismografo = new Estado("Fuera de Servicio", "Sismógrafo");
            Estado estadoOnlineSismografo = new Estado("Online", "Sismógrafo");

            Estado.agregarEstado(estadoCompletamenteRealizada);
            Estado.agregarEstado(estadoCerradaOrden);
            Estado.agregarEstado(estadoFueraDeServicioSismografo);
            Estado.agregarEstado(estadoOnlineSismografo);

            // Crear estaciones sismológicas
            EstacionSismologica estacion1 = new EstacionSismologica("Estación Central", "ESC01", "DOC123", LocalDateTime.now().minusDays(30), -34.6037F, -58.3816F, 1);
            EstacionSismologica estacion2 = new EstacionSismologica("Estación Norte", "ESN02", "DOC456", LocalDateTime.now().minusDays(25), -33.0000F, -59.0000F, 2);

            // Crear sismógrafos
            Sismografo sismografo1 = new Sismografo(LocalDateTime.now().minusYears(2), "SMG001", 1001, estacion1, estadoOnlineSismografo, LocalDateTime.now());
            Sismografo sismografo2 = new Sismografo(LocalDateTime.now().minusYears(1), "SMG002", 2002, estacion2, estadoOnlineSismografo, LocalDateTime.now());
            Sismografo.agregarSismografo(sismografo1);
            Sismografo.agregarSismografo(sismografo2);

            // Crear órdenes de inspección COMPLETAMENTE REALIZADAS
            OrdenDeInspeccion orden1 = new OrdenDeInspeccion(LocalDateTime.now().minusDays(7), 1, estadoCompletamenteRealizada, empleadoRI, estacion1);
            orden1.setFechaHoraFinalizacion(LocalDateTime.now().minusDays(7).plusHours(3));
            OrdenDeInspeccion orden2 = new OrdenDeInspeccion(LocalDateTime.now().minusDays(5), 2, estadoCompletamenteRealizada, empleadoRI, estacion2);
            orden2.setFechaHoraFinalizacion(LocalDateTime.now().minusDays(5).plusHours(2));
            OrdenDeInspeccion orden3_cerrada = new OrdenDeInspeccion(LocalDateTime.now().minusDays(10), 3, estadoCompletamenteRealizada, empleadoRI, estacion1);
            orden3_cerrada.setFechaHoraFinalizacion(LocalDateTime.now().minusDays(10).plusHours(4));
            orden3_cerrada.cerrar(estadoCerradaOrden, "Cierre de prueba", LocalDateTime.now().minusDays(9));

            OrdenDeInspeccion.agregarOrdenDeInspeccion(orden1);
            OrdenDeInspeccion.agregarOrdenDeInspeccion(orden2);
            OrdenDeInspeccion.agregarOrdenDeInspeccion(orden3_cerrada);

            // Crear Monitores CCRS
            MonitorCCRS monitor1 = new MonitorCCRS("Monitor CCRS 1");
            MonitorCCRS monitor2 = new MonitorCCRS("Monitor CCRS 2");
            List<MonitorCCRS> pantallasCCRS = List.of(monitor1, monitor2);

            // Crear Interfaz de Notificación
            InterfazNotificacion interfazNotificacion = new InterfazNotificacion();

            // Crear Motivos de Fuera de Servicio
            MotivoTipo motivoTipo1 = new MotivoTipo("Falla de energía");
            MotivoTipo motivoTipo2 = new MotivoTipo("Problema de sensor");
            MotivoTipo.agregarMotivoTipoFueraServicio(motivoTipo1);
            MotivoTipo.agregarMotivoTipoFueraServicio(motivoTipo2);

            // Crear Gestor y Pantalla
            GestorCierreOrdenInspeccion gestor = new GestorCierreOrdenInspeccion(sesion, interfazNotificacion, pantallasCCRS);
            PantallaCierreOrdenInspeccion pantalla = new PantallaCierreOrdenInspeccion(gestor);
            gestor.setPantallaCierreOrdenInspeccion(pantalla);
            
            pantalla.setVisible(true); // Hacer la pantalla visible para la interacción
            pantalla.opcionCerrarOrdenDeInspeccion();
        });
    }
}