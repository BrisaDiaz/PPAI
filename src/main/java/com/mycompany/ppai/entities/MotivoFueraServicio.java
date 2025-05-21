package com.mycompany.ppai.entities;
 

 import java.util.Objects;
 

 public class MotivoFueraServicio {
  private String comentario;
  private MotivoTipo motivoTipo;
 

    // Constructor
  public MotivoFueraServicio(String comentario, MotivoTipo motivoTipo) {
  this.comentario = Objects.requireNonNull(comentario, "El comentario no puede ser nulo");
  this.motivoTipo = Objects.requireNonNull(motivoTipo, "El tipo de motivo no puede ser nulo");
  }
 
    // Getters
  public String getComentario() {
  return comentario;
  }
 
  public MotivoTipo getMotivoTipo() {
  return motivoTipo;
  }
 
    // Setters
  public void setComentario(String comentario) {
  this.comentario = Objects.requireNonNull(comentario, "El comentario no puede ser nulo");
  }
 
  public void setMotivoTipo(MotivoTipo motivoTipo) {
  this.motivoTipo = Objects.requireNonNull(motivoTipo, "El tipo de motivo no puede ser nulo");
  }
 }