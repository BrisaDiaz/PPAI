package com.mycompany.ppai.entities;
 

 import java.time.LocalDateTime;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Objects;
 import com.mycompany.ppai.entities.MotivoFueraServicio;
 

 public class CambioEstado {
  private LocalDateTime fechaHoraInicio;
  private LocalDateTime fechaHoraFin;
  private Estado estado;
  private List<MotivoFueraServicio> motivoFueraServicio;
  private Empleado responsableDeInspeccion;
 

  // Constructor para un CambioEstado general. 
  public CambioEstado(Estado estado, LocalDateTime fechaHoraInicio, Empleado responsableDeInspeccion) {
  this.estado = Objects.requireNonNull(estado, "El estado no puede ser nulo");
  this.fechaHoraInicio = Objects.requireNonNull(fechaHoraInicio, "La fecha y hora de inicio no puede ser nula");
  this.responsableDeInspeccion = Objects.requireNonNull(responsableDeInspeccion, "El responsable de la inspección no puede ser nulo");
  }
 

  // Constructor para un CambioEstado cuando el sismógrafo se pone Fuera de Servicio.
  
  public CambioEstado(Estado estado, LocalDateTime fechaHoraInicio, Empleado responsableDeInspeccion, List<Object[]> motivosFueraServicio) {
  this(estado, fechaHoraInicio, responsableDeInspeccion); // Llamar al constructor general
  this.registrarMotivosFueraDeServicio(motivosFueraServicio);
  }
 

  public LocalDateTime getFechaHoraInicio() {
  return fechaHoraInicio;
  }
 

  public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
  this.fechaHoraInicio = Objects.requireNonNull(fechaHoraInicio, "La fecha y hora de inicio no puede ser nula");
  }
 

  public LocalDateTime getFechaHoraFin() {
  return fechaHoraFin;
  }
 

  public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
  this.fechaHoraFin = fechaHoraFin;
  }
 

  public Estado getEstado() {
  return estado;
  }
 

  public void setEstado(Estado estado) {
  this.estado = Objects.requireNonNull(estado, "El estado no puede ser nulo");
  }
 

  public List<MotivoFueraServicio> getMotivoFueraServicio() {
  return motivoFueraServicio;
  }
 

  public void setMotivoFueraServicio(List<MotivoFueraServicio> motivoFueraServicio) {
  this.motivoFueraServicio = motivoFueraServicio;
  }
 

  public Empleado getResponsableDeInspeccion() {
  return responsableDeInspeccion;
  }
 

  public void setResponsableDeInspeccion(Empleado responsableDeInspeccion) {
  this.responsableDeInspeccion = Objects.requireNonNull(responsableDeInspeccion,
  "El responsable de la inspección no puede ser nulo");
  }
 

  // Métodos de comportamiento
 

  /**
  * Registra los motivos por los que el sismógrafo se puso Fuera de Servicio.
  *
  * @param motivosFueraServicio Lista de pares (MotivoTipo, Comentario) que describen los motivos.
  */
  public void registrarMotivosFueraDeServicio(List<Object[]> motivosFueraServicio) {
  this.motivoFueraServicio = new ArrayList<>();

  if (motivosFueraServicio != null) {

    for (Object[] motivoData : motivosFueraServicio) {
        MotivoTipo tipo = (MotivoTipo) motivoData[0];
        String comentario = (String) motivoData[1];
        
        MotivoFueraServicio motivo = new MotivoFueraServicio(comentario, tipo);
        this.motivoFueraServicio.add(motivo);
    }
  }
  }
 
  public boolean esCambioEstadoActual() {
  return this.fechaHoraFin == null;
  }
 }