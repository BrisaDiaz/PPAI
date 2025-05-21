package com.mycompany.ppai.controllers;

import com.mycompany.ppai.entities.OrdenDeInspeccion;
import com.mycompany.ppai.entities.Empleado;
import com.mycompany.ppai.entities.Sesion;
import com.mycompany.ppai.entities.MotivoTipo;
import com.mycompany.ppai.entities.Estado;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.List;
import com.google.gson.JsonObject; 
import java.util.ArrayList;

public class GestorCierreOrdenInspeccion {
    private LocalDateTime fechaHoraActual;
    private OrdenDeInspeccion selectOrdenDeInspeccion;
    private String observacionCierreOrden;
    private PantallaCierreOrdenInspeccion pantallaCierreOrdenInspeccion;
    private InterfazNotificacion interfazNotificacion;
    private List<PantallaCCRS> pantallasCCRS;
    private Sesion sesionActual;
    private Empleado empleadoLogeado;
    private List<Object[]> motivosFueraServicio;


    public GestorCierreOrdenInspeccion(Sesion sesionActual, PantallaCierreOrdenInspeccion pantallaCierreOrdenInspeccion, InterfazNotificacion interfazNotificacion, List<PantallaCCRS> pantallasCCRS) {
        this.pantallaCierreOrdenInspeccion = Objects.requireNonNull(pantallaCierreOrdenInspeccion, "La pantalla de cierre de orden de inspección no puede ser nula");
        this.interfazNotificacion = Objects.requireNonNull(interfazNotificacion, "La interfaz de notificación no puede ser nula");
        this.pantallasCCRS = Objects.requireNonNull(pantallasCCRS, "La lista de pantallas CCRS no puede ser nula");
        this.sesionActual = Objects.requireNonNull(sesionActual, "La sesión actual no puede ser nula");
        this.motivosFueraServicio = new ArrayList<>();
    }
     public void nuevoCierreOrdenInspeccion() {
        this.obtenerRILogeado();
        List<JsonObject> infoOrdenesInspeccion = this.mostrarInfoOrdenesInspeccion();
        this.pantallaCierreOrdenInspeccion.mostrarInfoOrdenesInspeccion(infoOrdenesInspeccion);
    }

    public void obtenerRILogeado() {
        this.empleadoLogeado = this.sesionActual.obtenerRILogeado();
    }

    public List<JsonObject> mostrarInfoOrdenesInspeccion() {
        List<OrdenDeInspeccion> ordenes = OrdenDeInspeccion.getTodasLasOrdenesDeInspeccion();
        List<JsonObject> ordenesFiltradas = new ArrayList<>()
        ;
        for (OrdenDeInspeccion orden : ordenes) {
            if (orden.esCompletamenteRealizada() && orden.esMiRI(this.empleadoLogeado)) {
                ordenesFiltradas.add(orden.mostrarDatosOrdenesDeInspeccion());
            }
        }

        this.ordenarOrdenesPorFechaFinalizacion(infoOrdenesInspeccionn);
    }

    public void ordenarOrdenesPorFechaFinalizacion(List<JsonObject> ordenes) {
         ordenes.sort((o1, o2) -> {
            LocalDateTime fechaHora1 = LocalDateTime.parse(o1.get("fechaHoraFinalizacion").getAsString());
            LocalDateTime fechaHora2 = LocalDateTime.parse(o2.get("fechaHoraFinalizacion").getAsString());
            return fechaHora2.compareTo(fechaHora1);
        });
    }

    public void tomarSelecOrdenInspeccion(Integer numeroOrden) {
       this.selectOrdenDeInspeccion = OrdenDeInspeccion.obtenerOrdenPorNumero(numeroOrden);
       this.pantallaCierreOrdenInspeccion.solicitarObservacionCierreOrden();
    }

    public void tomarObservacionCierreOrden(String observacion) {
        this.observacionCierreOrden = Objects.requireNonNull(observacion, "La observación de cierre no puede ser nula");
    }

    public void mostrarTiposMotivoFueraDeServicio() {
        List<MotivoTipo> todosMotivoTipo= MotivoTipo.obtenerTodosMotivosTipoFueraServicio();
        List<String> motivosTipoFueraServicio = new ArrayList<>();
        for (MotivoTipo motivo : todosMotivoTipo) {
            motivosTipoFueraServicio.add(motivo.getDescripcion());
        }
        this.pantallaCierreOrdenInspeccion.solicitarMotivosFueraDeServicio(motivosTipoFueraServicio);
    } 

    public void tomarMotivosFueraDeServicio(List<String[]> motivosFueraServicio) {
        for (String[] motivo : motivosSeleccionados) {
            String motivoTipoDescripcion = motivo[0];
            String motivoDescripcion = motivo[1];
            MotivoTipo motivoTipo = MotivoTipo.obtenerMotivoTipoPorDescripcion(motivoTipoDescripcion);
            if (motivoTipo != null) {
                this.motivosFueraServicio.add(new Object[]{motivoTipo, motivoDescripcion});
            }
    }
        this.pantallaCierreOrdenInspeccion.solicitarConfirmacionCierreOrden();
    }

    public void tomarConfirmacionCierreOrden() {
        if (this.validarObservacionCierreOrden() && this.validarSelecMotivoFueraDeServicio()) {
            this.cerrarOrdenDeInspeccion();
        } 
    }

    public boolean validarObservacionCierreOrden() {
        if (this.observacionCierreOrden == null || this.observacionCierreOrden.isEmpty()) {
            return false;
        }
        return true;
    }
    
    public boolean validarSelecMotivoFueraDeServicio() {
        if (this.motivosFueraServicio.isEmpty()) {
            return false;
        }
        return true;
    }

    public void cerrarOrdenDeInspeccion() {
        this.fechaHoraActual = LocalDateTime.now();
        List<Estado> todosLosEstados = Estado.obtenerTodosLosEstados();
        
        for (Estado estado : todosLosEstados) {
            if (estado.esAmbitoOrdenDeInspeccion() && estado.esCerrada()) {
               this.esCerrada = estado
                break;
            }
              
        }
        this.selectOrdenDeInspeccion.cerrar(this.esCerrada, this.observacionCierreOrden, this.fechaHoraActual);

        for (Estado estado : todosLosEstados) {
            if (estado.esAmbitoSismografo() && estado.esFueraDeServicio()) {
               this.estadoFueraServicio = estado
                break;
            }
              
        }
        this.selectOrdenDeInspeccion.actualizarSismografoFueraServicio(this.fechaHoraActual, this.empleadoLogeado, this.estadoFueraServicio, this.motivosFueraServicio);
        this.notificarResponsablesDeReparacion();
        this.publicarEnMonitoresCCRS();
    }

    public void notificarResponsablesDeReparacion(cuerpoNotificacion) {
       List<Empleado> todosLosEmpleados = Empleado.obtenerTodosLosEmpleados();
       List<String> mailsResponsables = new ArrayList<>();

        for (Empleado empleado : todosLosEmpleados) {
            if (empleado.esResponsableDeReparacion()) {
                mailsResponsables.add(empleado.obtenerMail());
            }
        }
        
        String cuerpoNotificacion = "Se ha cerrado una orden de inspección con motivos fuera de servicio.";
        interfazNotificacion.enviarNotificacion(mailsResponsables, "Cierre de Orden de Inspección", "Se ha cerrado una orden de inspección con motivos fuera de servicio.");
    }
}