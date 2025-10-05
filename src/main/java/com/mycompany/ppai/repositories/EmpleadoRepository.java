package com.mycompany.ppai.repositories;

import com.mycompany.ppai.entities.Empleado;

import jakarta.persistence.EntityManager;
import java.util.List;

public class EmpleadoRepository {

    private static EmpleadoRepository instance = null;

    private final EntityManager entityManager;

    private EmpleadoRepository(EntityManager em) {
        this.entityManager = em;
    }

    public static EmpleadoRepository getInstance(EntityManager em) {
        if (instance == null) {
            instance = new EmpleadoRepository(em);
        }
        return instance;
    }

    public List<Empleado> obtenerTodos() {
        return entityManager.createQuery("SELECT e FROM Empleado e", Empleado.class)
                .getResultList();
    }

    public Empleado guardar(Empleado empleado) {
        try {
            entityManager.getTransaction().begin();
            Empleado mergedOrden = entityManager.merge(empleado);
            entityManager.getTransaction().commit();
            return mergedOrden;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error el guardar el Empleado", e);
        }
    }

    public void guardarTodos(List<Empleado> listaEmpleados) {
        try {
            entityManager.getTransaction().begin();
            for (Empleado orden : listaEmpleados) {
                entityManager.merge(orden);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar la lista de Empleado", e);
        }
    }
}