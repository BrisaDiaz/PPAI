package com.mycompany.ppai.controllers;
 
 import com.mycompany.ppai.entities.OrdenDeInspeccion;
 import com.mycompany.ppai.entities.Empleado;
 import com.mycompany.ppai.entities.Sesion;
 import com.mycompany.ppai.entities.MotivoTipo;
 import com.mycompany.ppai.entities.Estado;
 import com.mycompany.ppai.entities.Sismografo;
 import com.mycompany.ppai.boundaries.InterfazNotificacion;
 import com.mycompany.ppai.boundaries.MonitorCCRS;
 import com.mycompany.ppai.boundaries.PantallaCierreOrdenInspeccion;
 import java.time.LocalDateTime;
 import java.util.Objects;
 import java.util.List;
 import com.google.gson.JsonObject;
 import java.util.ArrayList;
 import java.util.stream.Collectors;
 
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
     private boolean validacionObservacionOk;
     private boolean validacionSelecMotivoOk;
     private boolean validacionComentariosMotivosOk;
     private List<String> tiposMotivoFueraDeServicioCache;
 
     // Constructor
     public GestorCierreOrdenInspeccion(Sesion sesionActual, InterfazNotificacion interfazNotificacion, List<MonitorCCRS> pantallasCCRS) {
         this.interfazNotificacion = Objects.requireNonNull(interfazNotificacion, "La interfaz de notificación no puede ser nula");
         this.pantallasCCRS = Objects.requireNonNull(pantallasCCRS, "La lista de pantallas CCRS no puede ser nula");
         this.sesionActual = Objects.requireNonNull(sesionActual, "La sesión actual no puede ser nula");
         this.motivosFueraServicio = new ArrayList<>();
         this.ponerSismografoFueraServicio = false; // Default to not putting it out of service
         this.validacionObservacionOk = false;
         this.validacionSelecMotivoOk = false;
         this.validacionComentariosMotivosOk = false;
         this.tiposMotivoFueraDeServicioCache = new ArrayList<>();
     }
 
     public boolean esPonerSismografoFueraDeServicio() {
         return ponerSismografoFueraServicio;
     }
 
     public boolean esValidacionObservacionOk() {
         return validacionObservacionOk;
     }
 
     public boolean esValidacionSelecMotivoOk() {
         return validacionSelecMotivoOk;
     }
 
     public boolean esValidacionComentariosMotivosOk() {
         return validacionComentariosMotivosOk;
     }
 
     public void setPantallaCierreOrdenInspeccion(PantallaCierreOrdenInspeccion pantallaCierreOrdenInspeccion) {
         this.pantallaCierreOrdenInspeccion = Objects.requireNonNull(pantallaCierreOrdenInspeccion,
                 "La pantalla de cierre de orden de inspección no puede ser nula");
     }
     // Inicia el proceso de cierre de una orden de inspección.
     // Este método se llama desde la interfaz de usuario para iniciar el flujo de cierre de orden de inspección. (ejecutado desde pantallaCierreOrdenInspeccion)
     public void nuevoCierreOrdenInspeccion() {
         this.obtenerRILogeado();
         List<JsonObject> infoOrdenesInspeccion = this.mostrarInfoOrdenesInspeccion();
         this.pantallaCierreOrdenInspeccion.mostrarInfoOrdenesInspeccion(infoOrdenesInspeccion);
         this.tiposMotivoFueraDeServicioCache = obtenerTiposMotivoFueraDeServicio();
     }
 
     public void obtenerRILogeado() {
         this.empleadoLogeado = this.sesionActual.obtenerRILogeado();
     }
 
     public List<JsonObject> mostrarInfoOrdenesInspeccion() {
         List<OrdenDeInspeccion> ordenes = OrdenDeInspeccion.obtenerTodasOrdenesDeInspeccion();
         List<Sismografo> todosLosSismografos = Sismografo.obtenerTodosLosSismografos();
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
         this.observacionCierreOrden = observacion;
         this.ponerSismografoFueraServicio = ponerFueraDeServicio;
         this.validacionObservacionOk = true; // Asumimos que la observación ingresada es válida inicialmente
         if (this.ponerSismografoFueraServicio) {
             this.pantallaCierreOrdenInspeccion.solicitarMotivosFueraDeServicio(this.tiposMotivoFueraDeServicioCache);
         } else {
             this.pantallaCierreOrdenInspeccion.solicitarConfirmacionCierreOrden(); // Skip motive selection
         }
     }
 
     public List<String> obtenerTiposMotivoFueraDeServicio() {
         List<MotivoTipo> todosMotivoTipo = MotivoTipo.obtenerTodosMotivosTipoFueraServicio();
         return todosMotivoTipo.stream().map(MotivoTipo::getDescripcion).collect(Collectors.toList());
     }
 
     /**
      * Toma los motivos seleccionados por el usuario para poner el sismógrafo fuera de servicio.
      *
      * @param motivosSeleccionados Lista de arreglos de String, donde cada arreglo contiene
      * [Descripción del MotivoTipo, Comentario].
      */
     // Este método se llama cuando el usuario selecciona y comenta los motivos para poner el sismógrafo fuera de servicio (ejecutado desde pantallaCierreOrdenInspeccion).
     public void tomarMotivosFueraDeServicio(List<String[]> motivosSeleccionados) {
         this.motivosFueraServicio.clear();
         if (motivosSeleccionados != null) {
             this.validacionSelecMotivoOk = !motivosSeleccionados.isEmpty();
             boolean comentariosOk = true;
             for (String[] motivo : motivosSeleccionados) {
                 String motivoTipoDescripcion = motivo[0];
                 String motivoDescripcion = motivo[1];
                 MotivoTipo motivoTipo = MotivoTipo.obtenerMotivoTipoPorDescripcion(motivoTipoDescripcion);
                 if (motivoTipo != null) {
                     this.motivosFueraServicio.add(new Object[]{motivoTipo, motivoDescripcion});
                     if (motivoDescripcion == null || motivoDescripcion.trim().isEmpty()) {
                         comentariosOk = false;
                     }
                 }
             }
             this.validacionComentariosMotivosOk = comentariosOk;
         } else {
             this.validacionSelecMotivoOk = false;
             this.validacionComentariosMotivosOk = true; // Si no hay motivos, los comentarios están "ok" por omisión
         }
         this.pantallaCierreOrdenInspeccion.solicitarConfirmacionCierreOrden();
     }
 
     // Este método se llama cuando el usuario confirma el cierre de la orden de inspección (ejecutado desde pantallaCierreOrdenInspeccion).
     public boolean tomarConfirmacionCierreOrden(boolean confirmacion) {
         if (confirmacion) {
             this.validacionObservacionOk = validarObservacionCierreOrden();
             if (!this.validacionObservacionOk) {
                 this.pantallaCierreOrdenInspeccion.mostrarMensaje("Debe ingresar una observación para cerrar la orden.");
                 this.pantallaCierreOrdenInspeccion.solicitarObservacionCierreOrden();
                 return false;
             }
             if (this.ponerSismografoFueraServicio) {
                 this.validacionSelecMotivoOk = validarSelecMotivoFueraDeServicio();
                 if (!this.validacionSelecMotivoOk) {
                     this.pantallaCierreOrdenInspeccion.mostrarMensaje("Debe seleccionar al menos un motivo si pone el sismógrafo fuera de servicio.");
                     this.pantallaCierreOrdenInspeccion.solicitarMotivosFueraDeServicio(this.tiposMotivoFueraDeServicioCache);
                     return false;
                 }
                 this.validacionComentariosMotivosOk = validarComentariosMotivos();
                 if (!this.validacionComentariosMotivosOk) {
                     this.pantallaCierreOrdenInspeccion.mostrarMensaje("Los comentarios de los motivos no pueden estar vacíos.");
                     this.pantallaCierreOrdenInspeccion.solicitarMotivosFueraDeServicio(this.tiposMotivoFueraDeServicioCache);
                     return false;
                 }
             }
             this.cerrarOrdenDeInspeccion();
             String mensajeCierre = "Orden de inspección cerrada.";
             if (!this.ponerSismografoFueraServicio) {
                 mensajeCierre += " Sismógrafo se mantiene online.";
             }
             this.pantallaCierreOrdenInspeccion.mostrarMensaje(mensajeCierre);
             if (this.ponerSismografoFueraServicio) {
                 this.actualizarSismografoFueraDeServicio();
             } else {
                 // Método para poner el sismógrafo online (A2)
                 this.actualizarSismografoOnline();
             }
             this.finCU();
             return true;
         } else {
             // Método para cancelar el cierre de la orden de inspección (A7)
             this.pantallaCierreOrdenInspeccion.mostrarMensaje("Cierre de orden cancelado.");
             this.finCU();
             return true;
         }
     }
 
     public boolean validarObservacionCierreOrden() {
         return this.observacionCierreOrden != null && !this.observacionCierreOrden.trim().isEmpty();
     }
 
     public boolean validarSelecMotivoFueraDeServicio() {
         return !this.motivosFueraServicio.isEmpty();
     }
 
     public boolean validarComentariosMotivos() {
         if (this.ponerSismografoFueraServicio) {
             for (Object[] motivo : this.motivosFueraServicio) {
                 String comentario = (String) motivo[1];
                 if (comentario == null || comentario.trim().isEmpty()) {
                     return false;
                 }
             }
         }
         return true;
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
         List<Sismografo> todosLosSismografos = Sismografo.obtenerTodosLosSismografos();
         this.selectOrdenDeInspeccion.actualizarSismografoFueraServicio(this.fechaHoraActual, this.empleadoLogeado,
                 estadoFueraServicio, this.motivosFueraServicio, todosLosSismografos);
 
         String cuerpoNotificacion = "Se ha cerrado la orden de inspección número "
                 + this.selectOrdenDeInspeccion.getNumeroOrden()
                 + " con el sismógrafo (Identificador: "
                 + this.selectOrdenDeInspeccion.mostrarDatosOrdeneDeInspeccion(todosLosSismografos).get("identificadorSismografo").getAsString()
                 + ") en estado " + nombreEstadoFueraServicio
                 + " desde " + this.fechaHoraActual
                 + ". Motivos: " + this.obtenerDescripcionMotivos();
 
         this.pantallaCierreOrdenInspeccion.mostrarMensaje("Notificación enviada:\n" + cuerpoNotificacion);
 
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
         List<Sismografo> todosLosSismografos = Sismografo.obtenerTodosLosSismografos();
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
 
 }