package com.mycompany.ppai.repositories;

import com.mycompany.ppai.entities.Sesion;
import com.mycompany.ppai.entities.Sismografo;

import jakarta.persistence.EntityManager;
import java.util.List;

public class SesionRepository {
    private static SesionRepository instance = null;
    private final EntityManager entityManager;
        
    private SesionRepository(EntityManager em) {
        this.entityManager = em;
    }

    public static SesionRepository getInstance(EntityManager em) {
        if (instance == null) {
            instance = new SesionRepository(em);
        }
        return instance;
    }

    public List<Sesion> obtenerTodos() {
        return entityManager.createQuery("SELECT s FROM Sesion s", Sesion.class)
                .getResultList();
    }

    public Sesion guardar(Sesion sesion) {
        try {
            entityManager.getTransaction().begin();
            Sesion mergedSesion = entityManager.merge(sesion);
            entityManager.getTransaction().commit();
            return mergedSesion;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar la Sesion", e);
        }
    }
    
    public void guardarTodos(List<Sesion> listaSesiones) {
        try {
            entityManager.getTransaction().begin();
            for (Sesion sesion : listaSesiones) {
                entityManager.merge(sesion);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar la lista de Sesiones", e);
        }
    }
}