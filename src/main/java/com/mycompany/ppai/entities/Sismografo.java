package com.mycompany.ppai.entities;
 

 import java.time.LocalDateTime;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Objects;
 

 public class Sismografo {
  private LocalDateTime fechaAdquisicion;
  private String identificadorSismografo;
  private Integer nroSerie;
  private EstacionSismologica estacionSismologica;
  private List<CambioEstado> cambioEstado;
  private Estado estadoActual;

  // Constructor
  public Sismografo(LocalDateTime fechaAdquisicion, String identificadorSismografo, Integer nroSerie,
  EstacionSismologica estacionSismologica, Estado estadoInicial, LocalDateTime fechaHoraActual, Empleado responsableDeInspeccion) {

  this.fechaAdquisicion = Objects.requireNonNull(fechaAdquisicion, "La fecha de adquisición no puede ser nula");
  this.identificadorSismografo = Objects.requireNonNull(identificadorSismografo, "El identificador no puede ser nulo");
  this.nroSerie = Objects.requireNonNull(nroSerie, "El número de serie no puede ser nulo");
  this.estacionSismologica = Objects.requireNonNull(estacionSismologica, "La estación sismológica no puede ser nula");
  this.cambioEstado = new ArrayList<>();
  this.estadoActual = Objects.requireNonNull(estadoInicial, "El estado inicial no puede ser nulo");
  
  // Registrar el estado inicial directamente en el constructor
  CambioEstado cambioEstadoInicial = new CambioEstado(estadoInicial, fechaHoraActual, responsableDeInspeccion, null); // Suponiendo el constructor de CambioEstado
  this.cambioEstado.add(cambioEstadoInicial);
  }
 

  // Getters
 

  public LocalDateTime getFechaAdquisicion() {
  return fechaAdquisicion;
  }
 

  public String getIdentificador() {
  return identificadorSismografo;
  }
 

  public Integer getNroSerie() {
  return nroSerie;
  }
 

  public EstacionSismologica getEstacionSismologica() {
  return estacionSismologica;
  }
 

  public List<CambioEstado> getCambioEstado() {
  return cambioEstado;
  }
 

  public Estado getEstadoActual() {
  return estadoActual;
  }
 

  // Setters
 

  public void setFechaAdquisicion(LocalDateTime fechaAdquisicion) {
  this.fechaAdquisicion = Objects.requireNonNull(fechaAdquisicion, "La fecha de adquisición no puede ser nula");
  }
 

  public void setIdentificador(String identificadorSismografo) {
  this.identificadorSismografo = Objects.requireNonNull(identificadorSismografo, "El identificador no puede ser nulo");
  }
 

  public void setNroSerie(Integer nroSerie) {
  this.nroSerie = Objects.requireNonNull(nroSerie, "El número de serie no puede ser nulo");
  }
 

  public void setEstacionSismologica(EstacionSismologica estacionSismologica) {
  this.estacionSismologica = Objects.requireNonNull(estacionSismologica, "La estación sismológica no puede ser nula");
  }
 

  public void setCambioEstado(List<CambioEstado> cambioEstado) {
  this.cambioEstado = Objects.requireNonNull(cambioEstado, "La lista de cambios de estado no puede ser nula");
  }
 


  public void setEstadoActual(Estado estadoActual) {
  this.estadoActual = Objects.requireNonNull(estadoActual, "El estado actual no puede ser nulo");
  }
 

  // Métodos de comportamiento
 

  public boolean esMiEstacion(EstacionSismologica estacion) {
  return this.estacionSismologica.equals(estacion);
  }
 

  public void retirarDeServicio(LocalDateTime fechaHoraActual, Empleado responsableDeInspeccion,
  Estado estadoFueraServicio, List<Object[]> motivosFueraServicio) {

  this.finalizarCambioEstadoActual(fechaHoraActual);
  this.crearCambioEstado(estadoFueraServicio, fechaHoraActual, responsableDeInspeccion, motivosFueraServicio);
  }
 
  public void ponerOnline(LocalDateTime fechaHoraActual, Empleado responsableDeInspeccion,
  Estado estadoOnline) {

  this.finalizarCambioEstadoActual(fechaHoraActual);
  this.crearCambioEstado(estadoOnline, fechaHoraActual, responsableDeInspeccion);
  }

  public void finalizarCambioEstadoActual(LocalDateTime fechaHoraFin) {
    CambioEstado cambioEstadoActual = null;

    for (CambioEstado cambioEstadoIterado : this.cambioEstado) {
      if (cambioEstadoIterado.esCambioEstadoActual()) {
        cambioEstadoActual = cambioEstadoIterado;
        break;
      }
    }
    if (cambioEstadoActual == null) {
      throw new IllegalStateException("No hay un cambio de estado actual para finalizar.");
    }
    cambioEstadoActual.setFechaHoraFin(fechaHoraFin);
  }
 
  public void crearCambioEstado(Estado nuevoEstado, LocalDateTime fechaHoraActual, Empleado responsableDeInspeccion) {
  crearCambioEstado(nuevoEstado, fechaHoraActual, responsableDeInspeccion, null);
  }
 

  /**
  * Registra un cambio de estado del sismógrafo, incluyendo los motivos de fuera de
  * servicio.
  *
  * @param nuevoEstado El nuevo estado del sismógrafo.
  * @param fechaHoraActual La fecha y hora del cambio.
  * @param responsableDeInspeccion El empleado responsable de la inspección.
  * @param motivosFueraServicio Una lista de pares (MotivoTipo, Comentario) para
  * cuando el sismógrafo pasa a "Fuera de Servicio".
  */
  public void crearCambioEstado(Estado nuevoEstado, LocalDateTime fechaHoraActual,
    Empleado responsableDeInspeccion, List<Object[]> motivosFueraServicio) {

    if (nuevoEstado != null && !nuevoEstado.equals(this.estadoActual)) {
    
    CambioEstado nuevoCambioEstado = new CambioEstado(nuevoEstado, fechaHoraActual, responsableDeInspeccion, motivosFueraServicio);
    this.cambioEstado.add(nuevoCambioEstado);
    this.setEstadoActual(nuevoEstado);
    }
  }
 }