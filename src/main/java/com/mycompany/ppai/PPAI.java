package com.mycompany.ppai;

import com.mycompany.ppai.boundaries.InterfazNotificacion;
import com.mycompany.ppai.boundaries.MonitorCCRS;
import com.mycompany.ppai.boundaries.PantallaCierreOrdenInspeccion;
import com.mycompany.ppai.controllers.GestorCierreOrdenInspeccion;
import com.mycompany.ppai.entities.*;
import java.time.LocalDateTime;
import java.util.List;
import com.mycompany.ppai.repositories.*;

public class PPAI {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            // Crear roles
            Rol rolRI = new Rol("Responsable de Inspección", "Realiza inspecciones");
            Rol rolReparacion = new Rol("Responsable de Reparación", "Repara sismógrafos");

            // Crear empleados
            Empleado empleadoRI = new Empleado("Juan", "Pérez", "123456789", "juan.perez@example.com", rolRI);
            Empleado empleadoReparacion1 = new Empleado("Ana", "Gómez", "987654321", "ana.gomez@example.com", rolReparacion);
            EmpleadoRepository.guardarTodos(List.of(empleadoRI, empleadoReparacion1));

            // Crear usuarios
            Usuario usuarioRI = new Usuario("jperez", "password", empleadoRI);

            // Crear sesión
            Sesion sesion = new Sesion(LocalDateTime.now().minusHours(1), usuarioRI);

            // Crear estados
            Estado estadoCompletamenteRealizada = new Estado("Completamente Realizada", "Orden de Inspección");
            Estado estadoCerradaOrden = new Estado("Cerrada", "Orden de Inspección");
            Estado estadoFueraDeServicioSismografo = new Estado("Fuera de Servicio", "Sismógrafo");
            Estado estadoOnlineSismografo = new Estado("Online", "Sismógrafo");

            EstadoRepository.guardarTodos(List.of(estadoCompletamenteRealizada, estadoCerradaOrden, estadoFueraDeServicioSismografo, estadoOnlineSismografo));

            // Crear estaciones sismológicas
            EstacionSismologica estacion1 = new EstacionSismologica("Estación Central", "ESC01", "DOC123", LocalDateTime.now().minusDays(30), -34.6037F, -58.3816F, 1);
            EstacionSismologica estacion2 = new EstacionSismologica("Estación Norte", "ESN02", "DOC456", LocalDateTime.now().minusDays(25), -33.0000F, -59.0000F, 2);

            // Crear sismógrafos
            Sismografo sismografo1 = new Sismografo(LocalDateTime.now().minusYears(2), "SMG001", 1001, estacion1, estadoOnlineSismografo, LocalDateTime.now(), empleadoRI);
            Sismografo sismografo2 = new Sismografo(LocalDateTime.now().minusYears(1), "SMG002", 2002, estacion2, estadoOnlineSismografo, LocalDateTime.now(), empleadoRI);
            
            SismografoRepository.guardarTodos(List.of(sismografo1, sismografo2));

            // Crear órdenes de inspección COMPLETAMENTE REALIZADAS
            OrdenDeInspeccion orden1 = new OrdenDeInspeccion(LocalDateTime.now().minusDays(7), 1, estadoCompletamenteRealizada, empleadoRI, estacion1);
            orden1.setFechaHoraFinalizacion(LocalDateTime.now().minusDays(7).plusHours(3));
            OrdenDeInspeccion orden2 = new OrdenDeInspeccion(LocalDateTime.now().minusDays(5), 2, estadoCompletamenteRealizada, empleadoRI, estacion2);
            orden2.setFechaHoraFinalizacion(LocalDateTime.now().minusDays(5).plusHours(2));
            
            OrdenDeInspeccionRepository.guardarTodos(List.of(orden1, orden2));

            // Crear Monitores CCRS
            MonitorCCRS monitor = new MonitorCCRS();

            // Crear Interfaz de Notificación
            InterfazNotificacion interfazNotificacion = new InterfazNotificacion();

            // Crear Motivos de Fuera de Servicio
            MotivoTipo motivoTipo1 = new MotivoTipo("Falla de energía");
            MotivoTipo motivoTipo2 = new MotivoTipo("Problema de sensor");
            
            MotivoTipoRespository.guardarTodos(List.of(motivoTipo1, motivoTipo2));

            // Crear Gestor y Pantalla
            GestorCierreOrdenInspeccion gestor = new GestorCierreOrdenInspeccion(sesion, interfazNotificacion, monitor);
            PantallaCierreOrdenInspeccion pantalla = new PantallaCierreOrdenInspeccion(gestor);
            gestor.setPantallaCierreOrdenInspeccion(pantalla);
            
            pantalla.opcionCerrarOrdenDeInspeccion();
        });
    }
}