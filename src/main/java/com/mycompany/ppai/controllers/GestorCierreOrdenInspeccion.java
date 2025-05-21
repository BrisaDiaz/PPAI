package com.mycompany.ppai.controllers;

import com.mycompany.ppai.entities.OrdenDeInspeccion;
import com.mycompany.ppai.entities.Empleado;
import com.mycompany.ppai.entities.Sesion;
import com.mycompany.ppai.entities.MotivoTipo;
import com.mycompany.ppai.entities.Estado;
import com.mycompany.ppai.entities.Sismografo;

import com.mycompany.ppai.boundaries.InterfazNotificacion;
import com.mycompany.ppai.boundaries.MonitorCCRS;
import com.mycompany.ppai.boundaries.PantallaCierreOrdenInspeccion; // Import Pantalla

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
    private List<MonitorCCRS> pantallasCCRS;
    private Sesion sesionActual;
    private Empleado empleadoLogeado;
    private List<Object[]> motivosFueraServicio;
    private boolean ponerSismografoFueraServicio; // To track the user's choice

    // Constructor
    public GestorCierreOrdenInspeccion(Sesion sesionActual, PantallaCierreOrdenInspeccion pantallaCierreOrdenInspeccion,
                                        InterfazNotificacion interfazNotificacion, List<MonitorCCRS> pantallasCCRS) {
        this.pantallaCierreOrdenInspeccion = Objects.requireNonNull(pantallaCierreOrdenInspeccion,
                "La pantalla de cierre de orden de inspección no puede ser nula");
        this.interfazNotificacion = Objects.requireNonNull(interfazNotificacion, "La interfaz de notificación no puede ser nula");
        this.pantallasCCRS = Objects.requireNonNull(pantallasCCRS, "La lista de pantallas CCRS no puede ser nula");
        this.sesionActual = Objects.requireNonNull(sesionActual, "La sesión actual no puede ser nula");
        this.motivosFueraServicio = new ArrayList<>();
        this.ponerSismografoFueraServicio = false; // Default to not putting it out of service
    }

    // Inicia el proceso de cierre de una orden de inspección.
    // Este método se llama desde la interfaz de usuario para iniciar el flujo de cierre de orden de inspección. (ejecutado desde pantallaCierreOrdenInspeccion)
    public void nuevoCierreOrdenInspeccion() {
        this.obtenerRILogeado();
        List<JsonObject> infoOrdenesInspeccion = this.mostrarInfoOrdenesInspeccion();
        this.pantallaCierreOrdenInspeccion.mostrarInfoOrdenesInspeccion(infoOrdenesInspeccion);
    }

    public void obtenerRILogeado() {
        this.empleadoLogeado = this.sesionActual.obtenerRILogeado();
    }

    public List<JsonObject> mostrarInfoOrdenesInspeccion() {
        List<OrdenDeInspeccion> ordenes = OrdenDeInspeccion.obtenerTodasOrdenesDeInspeccion();
        List<Sismografo> todosLosSismografos = Sismografo.obtenerTodosSismografos();
        List<JsonObject> ordenesFiltradas = new ArrayList<>();

        for (OrdenDeInspeccion orden : ordenes) {
            if (orden.estoyCompletamenteRealizada() && orden.esMiRI(this.empleadoLogeado)) {
                ordenesFiltradas.add(orden.mostrarDatosOrdeneDeInspeccion(todosLosSismografos));
            }
        }
        this.ordenarOrdenesPorFechaFinalizacion(ordenesFiltradas);
        return ordenesFiltradas;
    }

    public void ordenarOrdenesPorFechaFinalizacion(List<JsonObject> ordenes) {
        ordenes.sort((o1, o2) -> {
            LocalDateTime fechaHora1 = LocalDateTime.parse(o1.get("fechaHoraFinalizacion").getAsString());
            LocalDateTime fechaHora2 = LocalDateTime.parse(o2.get("fechaHoraFinalizacion").getAsString());
            return fechaHora2.compareTo(fechaHora1); // Descending order
        });
    }

    // Este método se llama cuando el usuario selecciona una orden de inspección para cerrar (ejecutado desde pantallaCierreOrdenInspeccion).
    public void tomarSelecOrdenInspeccion(Integer numeroOrden) {
        this.selectOrdenDeInspeccion = OrdenDeInspeccion.obtenerOrdenPorNumero(numeroOrden);
        this.pantallaCierreOrdenInspeccion.solicitarObservacionCierreOrden();
    }

    // Modified to include the boolean for putting sismograph out of service
    public void tomarObservacionCierreOrden(String observacion, boolean ponerFueraDeServicio) {
        this.observacionCierreOrden = Objects.requireNonNull(observacion, "La observación de cierre no puede ser nula");
        this.ponerSismografoFueraServicio = ponerFueraDeServicio;
        if (this.ponerSismografoFueraServicio) {
            mostrarTiposMotivoFueraDeServicio();
        } else {
            this.pantallaCierreOrdenInspeccion.solicitarConfirmacionCierreOrden(); // Skip motive selection
        }
    }

    public void mostrarTiposMotivoFueraDeServicio() {
        List<MotivoTipo> todosMotivoTipo = MotivoTipo.obtenerTodosMotivosTipoFueraServicio();
        List<String> motivosTipoFueraServicio = new ArrayList<>();
        for (MotivoTipo motivo : todosMotivoTipo) {
            motivosTipoFueraServicio.add(motivo.getDescripcion());
        }
        this.pantallaCierreOrdenInspeccion.solicitarMotivosFueraDeServicio(motivosTipoFueraServicio);
    }

    /**
     * Toma los motivos seleccionados por el usuario para poner el sismógrafo fuera de servicio.
     *
     * @param motivosSeleccionados Lista de arreglos de String, donde cada arreglo contiene
     * [Descripción del MotivoTipo, Comentario].
     */
    // Este método se llama cuando el usuario selecciona y comenta los motivos para poner el sismógrafo fuera de servicio (ejecutado desde pantallaCierreOrdenInspeccion).
    public void tomarMotivosFueraDeServicio(List<String[]> motivosSeleccionados) {
        this.motivosFueraServicio.clear(); // Clear previous selections
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

    // Este método se llama cuando el usuario confirma el cierre de la orden de inspección (ejecutado desde pantallaCierreOrdenInspeccion).
    public void tomarConfirmacionCierreOrden(boolean confirmacion) {
        if (confirmacion) {
            if (!validarObservacionCierreOrden() && this.ponerSismografoFueraServicio) {
                this.pantallaCierreOrdenInspeccion.mostrarMensaje("Debe ingresar una observación para cerrar la orden.");
                this.pantallaCierreOrdenInspeccion.solicitarObservacionCierreOrden();
            } else if (this.ponerSismografoFueraServicio && !validarSelecMotivoFueraDeServicio()) {
                this.pantallaCierreOrdenInspeccion.mostrarMensaje("Debe seleccionar al menos un motivo para poner el sismógrafo fuera de servicio.");
                mostrarTiposMotivoFueraDeServicio();
            } else {
                this.cerrarOrdenDeInspeccion();
                if (this.ponerSismografoFueraServicio) {
                    this.actualizarSismografoFueraDeServicio();
                } else {
                    this.actualizarSismografoOnline();
                }
                finCU();
            }
        } else {
            this.pantallaCierreOrdenInspeccion.mostrarMensaje("Cierre de orden cancelado.");
            finCU();
        }
    }

    public boolean validarObservacionCierreOrden() {
        return this.observacionCierreOrden != null && !this.observacionCierreOrden.isEmpty();
    }

    public boolean validarSelecMotivoFueraDeServicio() {
        return !this.motivosFueraServicio.isEmpty();
    }

    public void cerrarOrdenDeInspeccion() {
        this.fechaHoraActual = LocalDateTime.now();
        List<Estado> todosLosEstados = Estado.obtenerTodosLosEstados();
        Estado estadoCerrada = null;
        for (Estado estado : todosLosEstados) {
            if (estado.esAmbitoOrdenDeInspeccion() && estado.esCerrada()) {
                estadoCerrada = estado;
                break;
            }
        }
        this.selectOrdenDeInspeccion.cerrar(estadoCerrada, this.observacionCierreOrden, this.fechaHoraActual);
    }

    public void actualizarSismografoFueraDeServicio() {
        List<Estado> todosLosEstados = Estado.obtenerTodosLosEstados();
        Estado estadoFueraServicio = null;
        String nombreEstadoFueraServicio = "";

        for (Estado estado : todosLosEstados) {
            if (estado.esAmbitoSismografo() && estado.esFueraDeServicio()) {
                estadoFueraServicio = estado;
                nombreEstadoFueraServicio = estado.getNombreEstado();
                break;
            }
        }
        List<Sismografo> todosLosSismografos = Sismografo.obtenerTodosSismografos();
        this.selectOrdenDeInspeccion.actualizarSismografoFueraServicio(this.fechaHoraActual, this.empleadoLogeado,
                estadoFueraServicio, this.motivosFueraServicio, todosLosSismografos);

        String cuerpoNotificacion = "Se ha cerrado la orden de inspección número "
                + this.selectOrdenDeInspeccion.getNumeroOrden()
                + " con el sismógrafo (Identificador: "
                + this.selectOrdenDeInspeccion.mostrarDatosOrdeneDeInspeccion(todosLosSismografos).get("identificadorSismografo").getAsString()
                + ") en estado " + nombreEstadoFueraServicio
                + " desde " + this.fechaHoraActual
                + ". Motivos: " + this.obtenerDescripcionMotivos();

        this.notificarResponsablesDeReparacion(cuerpoNotificacion);
        this.publicarEnMonitoresCCRS(cuerpoNotificacion);
    }

    public void actualizarSismografoOnline() {
        List<Estado> todosLosEstados = Estado.obtenerTodosLosEstados();
        Estado estadoOnline = null;
        String nombreEstadoOnline = "";

        for (Estado estado : todosLosEstados) {
            if (estado.esAmbitoSismografo() && estado.esOnline()) {
                estadoOnline = estado;
                nombreEstadoOnline = estado.getNombreEstado();
                break;
            }
        }
        List<Sismografo> todosLosSismografos = Sismografo.obtenerTodosSismografos();
        this.selectOrdenDeInspeccion.actualizarSismografoOnline(this.fechaHoraActual, this.empleadoLogeado, estadoOnline, todosLosSismografos);
        // No se notifica ni por pantalla ni por mail
    }

    private String obtenerDescripcionMotivos() {
        StringBuilder descripcionMotivos = new StringBuilder();
        for (Object[] motivo : this.motivosFueraServicio) {
            MotivoTipo motivoTipo = (MotivoTipo) motivo[0];
            String comentario = (String) motivo[1];
            descripcionMotivos.append("- ").append(motivoTipo.getDescripcion()).append(": ").append(comentario).append("\n");
        }
        return descripcionMotivos.toString();
    }

    public void notificarResponsablesDeReparacion(String cuerpoNotificacion) {
        List<Empleado> todosLosEmpleados = Empleado.obtenerTodosLosEmpleados();
        List<String> mailsResponsables = new ArrayList<>();

        for (Empleado empleado : todosLosEmpleados) {
            if (empleado.esResponsableDeReparacion()) {
                mailsResponsables.add(empleado.obtenerMail());
            }
        }
        interfazNotificacion.notificar(mailsResponsables, cuerpoNotificacion);
    }

    public void publicarEnMonitoresCCRS(String cuerpoNotificacion) {
        for (MonitorCCRS pantalla : pantallasCCRS) {
            pantalla.publicar(cuerpoNotificacion);
        }
    }

    public void finCU() {
        System.out.println("--------------------------------------------------");
        System.out.println("Fin del caso de uso: Cierre de Orden de Inspección");
        System.out.println("--------------------------------------------------");
    }

    // Method to simulate the "Actor cancels the execution of the use case" (A7)
    public void cancelarCierreOrden() {
        this.pantallaCierreOrdenInspeccion.mostrarMensaje("El cierre de la orden de inspección ha sido cancelado por el usuario.");
        finCU();
        // Optionally, you might want to reset some of the gestor's state here
        this.selectOrdenDeInspeccion = null;
        this.observacionCierreOrden = null;
        this.motivosFueraServicio.clear();
        this.ponerSismografoFueraServicio = false;
    }
}