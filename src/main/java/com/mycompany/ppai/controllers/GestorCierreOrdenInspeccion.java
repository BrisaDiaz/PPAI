package com.mycompany.ppai.controllers;
 

 import com.mycompany.ppai.entities.OrdenDeInspeccion;
 import com.mycompany.ppai.entities.Empleado;
 import com.mycompany.ppai.entities.Sesion;
 import com.mycompany.ppai.entities.MotivoTipo;
 import com.mycompany.ppai.entities.Estado;
 import com.mycompany.ppai.entities.Sismografo;

 import com.mycompany.ppai.boundaries.InterfazNotificacion;
 import com.mycompany.ppai.boundaries.MonitorCCRS;

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

  // Constructor
  public GestorCierreOrdenInspeccion(Sesion sesionActual, PantallaCierreOrdenInspeccion pantallaCierreOrdenInspeccion,
  InterfazNotificacion interfazNotificacion, List<MonitorCCRS> pantallasCCRS) {
  this.pantallaCierreOrdenInspeccion = Objects.requireNonNull(pantallaCierreOrdenInspeccion,
  "La pantalla de cierre de orden de inspección no puede ser nula");
  this.interfazNotificacion = Objects.requireNonNull(interfazNotificacion, "La interfaz de notificación no puede ser nula");
  this.pantallasCCRS = Objects.requireNonNull(pantallasCCRS, "La lista de pantallas CCRS no puede ser nula");
  this.sesionActual = Objects.requireNonNull(sesionActual, "La sesión actual no puede ser nula");
  this.motivosFueraServicio = new ArrayList<>();
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
  return fechaHora2.compareTo(fechaHora1);
  });
  }

  // Este método se llama cuando el usuario selecciona una orden de inspección para cerrar (ejecutado desde pantallaCierreOrdenInspeccion).
  public void tomarSelecOrdenInspeccion(Integer numeroOrden) {
  this.selectOrdenDeInspeccion = OrdenDeInspeccion.obtenerOrdenPorNumero(numeroOrden);
  this.pantallaCierreOrdenInspeccion.solicitarObservacionCierreOrden();
  }
 
  // Este método se llama cuando el usuario ingresa una observación para el cierre de la orden de inspección (ejecutado desde pantallaCierreOrdenInspeccion).
  public void tomarObservacionCierreOrden(String observacion) {
  this.observacionCierreOrden = Objects.requireNonNull(observacion, "La observación de cierre no puede ser nula");
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
  * @param motivosFueraServicio Lista de arreglos de String, donde cada arreglo contiene
  * [Descripción del MotivoTipo, Comentario].
  */
 // Este método se llama cuando el usuario selecciona y comenta los motivos para poner el sismógrafo fuera de servicio (ejecutado desde pantallaCierreOrdenInspeccion).
  public void tomarMotivosFueraDeServicio(List<String[]> motivosSeleccionados) {
  for (String[] motivo : motivosSeleccionados) {
  String motivoTipoDescripcion = motivo[0];
  String motivoDescripcion = motivo[1];
  MotivoTipo motivoTipo = MotivoTipo.obtenerMotivoTipoPorDescripcion(motivoTipoDescripcion);
  if (motivoTipo != null) {
  this.motivosFueraServicio.add(new Object[] { motivoTipo, motivoDescripcion });
  }
  }
  this.pantallaCierreOrdenInspeccion.solicitarConfirmacionCierreOrden();
  }
 
 // Este método se llama cuando el usuario confirma el cierre de la orden de inspección (ejecutado desde pantallaCierreOrdenInspeccion).
  public void tomarConfirmacionCierreOrden(boolean confirmacion) {
  if (confirmacion) {
  if (this.validarObservacionCierreOrden() && this.validarSelecMotivoFueraDeServicio()) {
        this.cerrarOrdenDeInspeccion();
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
  List<Sismografo> todosLosSismografos = Sismografo.obtenerTodosSismografos();


  Estado estadoCerrada = null;
  for (Estado estado : todosLosEstados) {
  if (estado.esAmbitoOrdenDeInspeccion() && estado.esCerrada()) {
  estadoCerrada = estado;
  break;
  }
  }
  this.selectOrdenDeInspeccion.cerrar(estadoCerrada, this.observacionCierreOrden, this.fechaHoraActual);

  Estado estadoFueraServicio = null;
  String nombreEstadoFueraServicio = "";

  for (Estado estado : todosLosEstados) {
  if (estado.esAmbitoSismografo() && estado.esFueraDeServicio()) {
  estadoFueraServicio = estado;
  nombreEstadoFueraServicio = estado.getNombreEstado();
  break;
  }
  }

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
 }

 public void finCU() {
    System.out.println("--------------------------------------------------");
    System.out.println("Fin del caso de uso: Cierre de Orden de Inspección");
    System.out.println("--------------------------------------------------");
}

}