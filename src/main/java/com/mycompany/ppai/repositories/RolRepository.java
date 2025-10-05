package com.mycompany.ppai.repositories;

import java.util.List;

import com.mycompany.ppai.entities.Rol;

import jakarta.persistence.EntityManager;

public class RolRepository {
    private static RolRepository instance = null;

    private final EntityManager entityManager;

    private RolRepository(EntityManager em) {
        this.entityManager = em;
    }

    public static RolRepository getInstance(EntityManager em) {
        if (instance == null) {
            instance = new RolRepository(em);
        }
        return instance;
    }

    public List<Rol> obtenerTodos() {
        return entityManager.createQuery("SELECT r FROM Rol r", Rol.class)
                .getResultList();
    }

    public Rol guardar(Rol rol) {
        try {
            entityManager.getTransaction().begin();
            Rol mergedOrden = entityManager.merge(rol);
            entityManager.getTransaction().commit();
            return mergedOrden;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error el guardar el Rol", e);
        }
    }

    public void guardarTodos(List<Rol> listaRols) {
        try {
            entityManager.getTransaction().begin();
            for (Rol orden : listaRols) {
                entityManager.merge(orden);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar la lista de Rol", e);
        }
    }
}
