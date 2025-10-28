package com.mycompany.ppai.controllers;
 
 import com.mycompany.ppai.entities.OrdenDeInspeccion;
 import com.mycompany.ppai.entities.Empleado;
 import com.mycompany.ppai.entities.Sesion;
 import com.mycompany.ppai.entities.MotivoTipo;
 import com.mycompany.ppai.entities.Estado;
 import com.mycompany.ppai.entities.Sismografo;
 import com.mycompany.ppai.boundaries.NotificadorResponsableReparacion;
 import com.mycompany.ppai.boundaries.IObservadorSismografo;
 import com.mycompany.ppai.boundaries.MonitorCCRS;
 import com.mycompany.ppai.boundaries.PantallaCierreOrdenInspeccion;

 import java.time.LocalDateTime;
import java.util.Objects;
 import java.util.List;

 import com.google.gson.JsonObject;

 import java.util.ArrayList;
 import java.util.stream.Collectors;

 import com.mycompany.ppai.repositories.*;


public class GestorCierreOrdenInspeccion implements ISujetoSismografo {
     private LocalDateTime fechaHoraActual;
     private OrdenDeInspeccion selecOrdenInspeccion;
     private String observacionCierreOrden;
     private PantallaCierreOrdenInspeccion pantallaCierreOrdenInspeccion;
     private MonitorCCRS monitorCCRS;
     private Sesion sesionActual;
     private Empleado empleadoLogeado;
     private boolean ponerSismografoFueraServicio;
     private List<String> comentariosMotivosFueraServicio;
     private List<MotivoTipo> selectMotivosFueraServicio;
     private String nombreNuevoEstadoSismografo;
     private String identificadorSismografo;
     private static EstadoRepository estadoRepository;
     private static OrdenDeInspeccionRepository orderRepository;
     private static SismografoRepository sismografoRepository;
     private static EmpleadoRepository empleadoRepository;
     private static MotivoTipoRespository motivoTipoRepository;

     // Lista de observadores
     private List<IObservadorSismografo> observadores = new ArrayList<>();

     /// atributos de validación
     private boolean validacionObservacionOk;
     private boolean validacionSelecMotivoOk;
     private boolean validacionComentariosMotivosOk;
 
     // Constructor
     public GestorCierreOrdenInspeccion(Sesion sesionActual,
             MonitorCCRS monitorCCRS, EstadoRepository estadoRepository, OrdenDeInspeccionRepository orderRepository, 
             SismografoRepository sismografoRepository, EmpleadoRepository empleadoRepository,
             MotivoTipoRespository motivoTipoRepository) {

         this.monitorCCRS = monitorCCRS;
         this.sesionActual = sesionActual;
         this.selectMotivosFueraServicio = new ArrayList<>();
         this.comentariosMotivosFueraServicio = new ArrayList<>();
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
     
     public void subscribir(List<IObservadorSismografo> observador) {
         this.observadores = observador;
     }

     public void quitar(IObservadorSismografo observador) {
         this.observadores.remove(observador);
     }

     public void notificar() {
         for (IObservadorSismografo observador : observadores) {
             observador.actualizar(this.identificadorSismografo, this.fechaHoraActual,
                     this.nombreNuevoEstadoSismografo,
                     selectMotivosFueraServicio.stream().map(MotivoTipo::getDescripcion).collect(Collectors.toList()),
                     this.comentariosMotivosFueraServicio);
         }
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
     public void tomarMotivosFueraDeServicio(List<String[]> motivosYComentarios) {
         this.selectMotivosFueraServicio.clear();
         this.comentariosMotivosFueraServicio.clear();

         if (motivosYComentarios != null) {
             for (String[] motivo : motivosYComentarios) {
                 String motivoTipoDescripcion = motivo[0];
                 String motivoComentario = motivo[1];
                 MotivoTipo motivoTipo = motivoTipoRepository.obtenerPorDescripcion(motivoTipoDescripcion);
                 if (motivoTipo != null) {
                     this.selectMotivosFueraServicio.add(motivoTipo);
                     this.comentariosMotivosFueraServicio.add(motivoComentario);
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
             // Método para cerrar la orden de inspección
             this.cerrarOrdenDeInspeccion();

             // Actualizar el estado del sismógrafo asociado
             if (!this.ponerSismografoFueraServicio) {
                // Método para poner el sismógrafo online (A2)
                this.actualizarSismografoAOnline();
            } else {
                // Método para poner el sismógrafo fuera de servicio - CU
                this.actualizarSismografoAFueraDeServicio();
             }

             orderRepository.guardar(this.selecOrdenInspeccion);

             this.finCU();
    
             return true;
         } else {
             // Método para cancelar el cierre de la orden de inspección (A7)
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
         if (this.comentariosMotivosFueraServicio != null && !this.comentariosMotivosFueraServicio.isEmpty() &&
         this.selectMotivosFueraServicio.size() == this.comentariosMotivosFueraServicio.size()) {
             this.validacionSelecMotivoOk = true;
            boolean comentariosOk = true;
            for (String motivoComentario : this.comentariosMotivosFueraServicio) {
                if (motivoComentario == null || motivoComentario.trim().isEmpty()) {
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
        // Obtener la fecha y hora actual
        this.getFechaHoraActual();
         // Obtener el estado "Cerrada"
         List<Estado> todosLosEstados = estadoRepository.obtenerTodos();
         Estado estadoCerrada = null;
         for (Estado estado : todosLosEstados) {
             if (estado.esAmbitoOrdenDeInspeccion() && estado.esCerrada()) {
                 estadoCerrada = estado;
                 break;
             }
         }
         // Cerrar la orden de inspección
         this.selecOrdenInspeccion.cerrar(estadoCerrada, this.observacionCierreOrden, this.fechaHoraActual);
     }
 
     public void actualizarSismografoAFueraDeServicio() {
         List<Estado> todosLosEstados = estadoRepository.obtenerTodos();
         Estado estadoFueraServicio = null;
 
         for (Estado estado : todosLosEstados) {
             if (estado.esAmbitoSismografo() && estado.esFueraDeServicio()) {
                 estadoFueraServicio = estado;
                 this.nombreNuevoEstadoSismografo = estado.getNombreEstado().toString();
                 break;
             }
         }
         List<Sismografo> todosLosSismografos = sismografoRepository.obtenerTodos();
         this.selecOrdenInspeccion.actualizarSismografoFueraServicio(this.fechaHoraActual, this.empleadoLogeado,
                 estadoFueraServicio, this.selectMotivosFueraServicio, this.comentariosMotivosFueraServicio,
                 todosLosSismografos);
                 
        // Obtener identificador del sismógrafo afectado
        this.identificadorSismografo = this.selecOrdenInspeccion.obtenerIdentificadorSismografo(todosLosSismografos);
 
        // Obtener mails de responsables de reparación
        List<String> mailsResponsables = obtenerMailsResponsablesDeReparacion();

        // Crear notificador
        NotificadorResponsableReparacion notificadorResponsableReparacion = new NotificadorResponsableReparacion(
                mailsResponsables);

        // subscribir notificador y monitor al gestor
        List<IObservadorSismografo> observadores = new ArrayList<>();
        observadores.add(notificadorResponsableReparacion);
        observadores.add(this.monitorCCRS);

        this.subscribir(observadores);

        // Notificar cambios de estado de sismógrafos
        this.notificar();
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

     public List<String> obtenerMailsResponsablesDeReparacion() {
         List<Empleado> todosLosEmpleados = empleadoRepository.obtenerTodos();
         List<String> mailsResponsables = new ArrayList<>();

         for (Empleado empleado : todosLosEmpleados) {
             if (empleado.esResponsableDeReparacion()) {
                 mailsResponsables.add(empleado.getMail());
             }
         }
         return mailsResponsables;
     }
 
     public void finCU() {
         System.out.println("--------------------------------------------------");
         System.out.println("Fin del caso de uso: Cierre de Orden de Inspección");
         System.out.println("--------------------------------------------------");
     }
 
 }