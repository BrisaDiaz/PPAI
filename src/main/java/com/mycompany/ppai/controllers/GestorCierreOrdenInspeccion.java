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

 import com.mycompany.ppai.repositories.*;

import jakarta.persistence.EntityManager;


 public class GestorCierreOrdenInspeccion {
     private LocalDateTime fechaHoraActual;
     private OrdenDeInspeccion selecOrdenInspeccion;
     private String observacionCierreOrden;
     private PantallaCierreOrdenInspeccion pantallaCierreOrdenInspeccion;
     private InterfazNotificacion interfazNotificacion;
     private MonitorCCRS monitorCCRS;
     private Sesion sesionActual;
     private Empleado empleadoLogeado;
     private boolean ponerSismografoFueraServicio;
     private List<Object[]> motivosFueraServicio;
     private static EstadoRepository estadoRepository;
     private static OrdenDeInspeccionRepository orderRepository;
     private static SismografoRepository sismografoRepository;
     private static EmpleadoRepository empleadoRepository;
     private static MotivoTipoRespository motivoTipoRepository;

     /// atributos de validación
     private boolean validacionObservacionOk;
     private boolean validacionSelecMotivoOk;
     private boolean validacionComentariosMotivosOk;
 
     // Constructor
     public GestorCierreOrdenInspeccion(Sesion sesionActual, InterfazNotificacion interfazNotificacion,
             MonitorCCRS monitorCCRS, EstadoRepository estadoRepository, OrdenDeInspeccionRepository orderRepository, 
             SismografoRepository sismografoRepository, EmpleadoRepository empleadoRepository,
             MotivoTipoRespository motivoTipoRepository) {
         
         this.interfazNotificacion = interfazNotificacion;
         this.monitorCCRS = monitorCCRS;
         this.sesionActual = sesionActual;
         this.motivosFueraServicio = new ArrayList<>();
         this.ponerSismografoFueraServicio = false;
         this.validacionObservacionOk = false;
         this.validacionSelecMotivoOk = false;
         this.validacionComentariosMotivosOk = false;
         this.estadoRepository = estadoRepository;
         this.orderRepository = orderRepository;
         this.sismografoRepository = sismografoRepository;
         this.empleadoRepository = empleadoRepository;
         this.motivoTipoRepository = motivoTipoRepository;
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
     public void nuevoCierreOrdenInspeccion() {
         this.obtenerRILogeado();
         List<JsonObject> infoOrdenesInspeccion = this.mostrarInfoOrdenesInspeccion();
         this.pantallaCierreOrdenInspeccion.mostrarInfoOrdenesInspeccion(infoOrdenesInspeccion);
     }
 
     public void obtenerRILogeado() {
         this.empleadoLogeado = this.sesionActual.obtenerRILogeado();
     }
 
     public List<JsonObject> mostrarInfoOrdenesInspeccion() {
         List<OrdenDeInspeccion> ordenes = orderRepository.obtenerTodos();
         List<Sismografo> sismografos = sismografoRepository.obtenerTodos();
         List<JsonObject> inforOrdenes = new ArrayList<>();

         for (OrdenDeInspeccion orden : ordenes) {
             if (orden.estoyCompletamenteRealizada() && orden.esMiRI(this.empleadoLogeado)) {
                inforOrdenes.add(orden.mostrarDatosOrdeneDeInspeccion(sismografos));
             }
         }
         this.ordenarOrdenesPorFechaFinalizacion(inforOrdenes);
         return inforOrdenes;
     }
 
     public void ordenarOrdenesPorFechaFinalizacion(List<JsonObject> ordenes) {
         ordenes.sort((o1, o2) -> {
             LocalDateTime fechaHora1 = LocalDateTime.parse(o1.get("fechaHoraFinalizacion").getAsString());
             LocalDateTime fechaHora2 = LocalDateTime.parse(o2.get("fechaHoraFinalizacion").getAsString());
             return fechaHora2.compareTo(fechaHora1);
         });
     }
 
     // Este método se llama cuando el usuario selecciona una orden de inspección para cerrar (ejecutado desde pantallaCierreOrdenInspeccion).
     public void tomarSelecOrdenInspeccion(Integer numeroOrden) {
         this.selecOrdenInspeccion = orderRepository.obtenerPorNumero(numeroOrden);
         this.pantallaCierreOrdenInspeccion.solicitarObservacionCierreOrden();
     }
 
     public void tomarObservacionCierreOrden(String observacion, boolean ponerFueraDeServicio) {
         this.observacionCierreOrden = observacion;
         this.ponerSismografoFueraServicio = ponerFueraDeServicio;
         this.validacionObservacionOk = true; // Asumimos que la observación ingresada es válida inicialmente

         if (this.ponerSismografoFueraServicio) {
            List<String> tiposMotivoFueraDeServicio = mostrarTiposMotivoFueraDeServicio();
            this.pantallaCierreOrdenInspeccion.solicitarMotivosFueraDeServicio(tiposMotivoFueraDeServicio);
         } else {
             this.pantallaCierreOrdenInspeccion.solicitarConfirmacionCierreOrden();
         }
     }
 
     public List<String> mostrarTiposMotivoFueraDeServicio() {
         List<MotivoTipo> todosMotivoTipo = motivoTipoRepository.obtenerTodos();
         return todosMotivoTipo.stream().map(MotivoTipo::getDescripcion).collect(Collectors.toList());
     }
 
     /**
      * Toma los motivos seleccionados por el usuario para poner el sismógrafo fuera de servicio.
      *
      * @param motivosSeleccionados Lista de arreglos de String, donde cada arreglo contiene
      * [Descripción del MotivoTipo, Comentario].
      */
     public void tomarMotivosFueraDeServicio(List<String[]> motivosSeleccionados) {
         this.motivosFueraServicio.clear();
         if (motivosSeleccionados != null) {
             for (String[] motivo : motivosSeleccionados) {
                 String motivoTipoDescripcion = motivo[0];
                 String motivoDescripcion = motivo[1];
                 MotivoTipo motivoTipo = motivoTipoRepository.obtenerPorDescripcion(motivoTipoDescripcion);
                 if (motivoTipo != null) {
                     this.motivosFueraServicio.add(new Object[]{motivoTipo, motivoDescripcion});
                 }
             }
         }
         this.pantallaCierreOrdenInspeccion.solicitarConfirmacionCierreOrden();
     }
 
     public boolean tomarConfirmacionCierreOrden(boolean confirmacion) {
         if (confirmacion) {
                // Validar la observación de cierre de orden (A3)
             validarObservacionCierre();
             if (!this.validacionObservacionOk) {
                 this.pantallaCierreOrdenInspeccion.solicitarObservacionCierreOrden();
                 return false;
             }
             if (this.ponerSismografoFueraServicio) {
                 validarSelecMotivoFueraDeServicio();
                 boolean validacionesMotivoOk = this.validacionSelecMotivoOk && this.validacionComentariosMotivosOk;

                 if (!validacionesMotivoOk) {
                    List<String> tiposMotivoFueraDeServicio = mostrarTiposMotivoFueraDeServicio();
                    this.pantallaCierreOrdenInspeccion.solicitarMotivosFueraDeServicio(tiposMotivoFueraDeServicio);
                    return false;
                 }
             }
             this.cerrarOrdenDeInspeccion();

             String mensajeCierre = "Orden de inspección cerrada.";

             if (!this.ponerSismografoFueraServicio) {
                // Método para poner el sismógrafo online (A2)
                this.actualizarSismografoAOnline();
                 mensajeCierre += " El sismógrafo se mantiene online.";
             } else {
                 this.actualizarSismografoAFueraDeServicio();
                 mensajeCierre += " El sismógrafo fué puesto fuera de servicio.";
             }

             orderRepository.guardar(this.selecOrdenInspeccion);
             
             this.pantallaCierreOrdenInspeccion.mostrarMensaje(mensajeCierre);

             this.finCU();
             return true;
         } else {
             // Método para cancelar el cierre de la orden de inspección (A7)
             this.pantallaCierreOrdenInspeccion.mostrarMensaje("Cierre de orden cancelado.");
             this.finCU();
             return true;
         }
     }
 
     public void validarObservacionCierre() {
         if (this.observacionCierreOrden != null && !this.observacionCierreOrden.trim().isEmpty()) {
             this.validacionObservacionOk = true;
         } else {
             this.validacionObservacionOk = false;
         }
     }
 
     public void validarSelecMotivoFueraDeServicio() {
         if (this.motivosFueraServicio != null && !this.motivosFueraServicio.isEmpty()) {
             this.validacionSelecMotivoOk = true;
            boolean comentariosOk = true;
            for (Object[] motivo : this.motivosFueraServicio) {
                String motivoDescripcion = (String) motivo[1];
                if (motivoDescripcion == null || motivoDescripcion.trim().isEmpty()) {
                    comentariosOk = false;
                    break;
                }
            }
            this.validacionComentariosMotivosOk = comentariosOk;
        } else {
            this.validacionSelecMotivoOk = false;
            this.validacionComentariosMotivosOk = true; // Si no hay motivos, los comentarios están "ok" por omisión
        }

     }

     public void getFechaHoraActual() {
         this.fechaHoraActual = LocalDateTime.now();
     }
 
     public void cerrarOrdenDeInspeccion() {
         this.getFechaHoraActual();

         List<Estado> todosLosEstados = estadoRepository.obtenerTodos();
         Estado estadoCerrada = null;
         for (Estado estado : todosLosEstados) {
             if (estado.esAmbitoOrdenDeInspeccion() && estado.esCerrada()) {
                 estadoCerrada = estado;
                 break;
             }
         }
         this.selecOrdenInspeccion.cerrar(estadoCerrada, this.observacionCierreOrden, this.fechaHoraActual);
     }
 
     public void actualizarSismografoAFueraDeServicio() {
         List<Estado> todosLosEstados = estadoRepository.obtenerTodos();
         Estado estadoFueraServicio = null;
         String nombreEstadoFueraServicio = "";
 
         for (Estado estado : todosLosEstados) {
             if (estado.esAmbitoSismografo() && estado.esFueraDeServicio()) {
                 estadoFueraServicio = estado;
                 nombreEstadoFueraServicio = estado.getNombreEstado().toString();
                 break;
             }
         }
         List<Sismografo> todosLosSismografos = sismografoRepository.obtenerTodos();
         this.selecOrdenInspeccion.actualizarSismografoFueraServicio(this.fechaHoraActual, this.empleadoLogeado,
                 estadoFueraServicio, this.motivosFueraServicio, todosLosSismografos);
 
        String plantilla = 
            "Se ha cerrado la orden de inspección número %d.\n" +
            "Sismógrafo %s actualizado al estado: **%s**.\n" +
            "Fecha y Hora de Cierre: %s.\n" +
            "Motivos del Cierre:\n%s";
         String cuerpoNotificacion = String.format(plantilla, 
                this.selecOrdenInspeccion.getNumeroOrden(),
                this.selecOrdenInspeccion.mostrarDatosOrdeneDeInspeccion(todosLosSismografos).get("identificadorSismografo").getAsString(),
                nombreEstadoFueraServicio,
                this.fechaHoraActual,
                this.obtenerDescripcionMotivos()
                );
 
         this.pantallaCierreOrdenInspeccion.mostrarMensaje("Notificación enviada:\n" + cuerpoNotificacion);
 
         this.notificarResponsablesDeReparacion(cuerpoNotificacion);
         this.publicarEnMonitoresCCRS(cuerpoNotificacion);
     }
 
     public void actualizarSismografoAOnline() {
         List<Estado> todosLosEstados = estadoRepository.obtenerTodos();
         Estado estadoOnline = null;
 
         for (Estado estado : todosLosEstados) {
             if (estado.esAmbitoSismografo() && estado.esOnline()) {
                 estadoOnline = estado;
                 break;
             }
         }
         List<Sismografo> todosLosSismografos = sismografoRepository.obtenerTodos();
         this.selecOrdenInspeccion.actualizarSismografoOnline(this.fechaHoraActual, this.empleadoLogeado, estadoOnline, todosLosSismografos);
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
         List<Empleado> todosLosEmpleados = empleadoRepository.obtenerTodos();
         List<String> mailsResponsables = new ArrayList<>();
 
         for (Empleado empleado : todosLosEmpleados) {
             if (empleado.esResponsableDeReparacion()) {
                 mailsResponsables.add(empleado.getMail());
             }
         }
         interfazNotificacion.enviarNotificacion(mailsResponsables, cuerpoNotificacion);
     }
 
     public void publicarEnMonitoresCCRS(String cuerpoNotificacion) {
        monitorCCRS.publicar(cuerpoNotificacion);
     }
 
     public void finCU() {
         System.out.println("--------------------------------------------------");
         System.out.println("Fin del caso de uso: Cierre de Orden de Inspección");
         System.out.println("--------------------------------------------------");
     }
 
 }