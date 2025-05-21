package com.mycompany.ppai.entities;
 

import java.util.ArrayList;
import java.util.List;
 import java.util.Objects;
 

 public class MotivoTipo {
 private String descripcion;

  // Simulación de persistencia
  private static final List<MotivoTipo> todosMotivosTipoFueraServicio = new ArrayList<>();
 
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

  // Simulación de persistencia
    public static List<MotivoTipo> obtenerTodosMotivosTipoFueraServicio() {
        return todosMotivosTipoFueraServicio;
    }
    public static void agregarMotivoTipoFueraServicio(MotivoTipo motivoTipo) {
        todosMotivosTipoFueraServicio.add(Objects.requireNonNull(motivoTipo, "El motivo tipo no puede ser nulo"));
    }

    public static MotivoTipo obtenerMotivoTipoPorDescripcion(String descripcion) {
        for (MotivoTipo motivoTipo : todosMotivosTipoFueraServicio) {
            if (motivoTipo.getDescripcion().equalsIgnoreCase(descripcion)) {
                return motivoTipo;
            }
        }
        return null; 
    }
 }