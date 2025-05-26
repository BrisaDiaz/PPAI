package com.mycompany.ppai.entities;
 
 import java.util.Objects;
 

 public class MotivoTipo {
    private String descripcion;
 
  // Constructor 
  public MotivoTipo(String descripcion) {
  this.descripcion = Objects.requireNonNull(descripcion, "La descripción no puede ser nula");
  }
 

  // Getters
  public String getDescripcion() {
  return descripcion;
  }
 

   // Setters
  public void setDescripcion(String descripcion) {
  this.descripcion = Objects.requireNonNull(descripcion, "La descripción no puede ser nula");
  }
 }