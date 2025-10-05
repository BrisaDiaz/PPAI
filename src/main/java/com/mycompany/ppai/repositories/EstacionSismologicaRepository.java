package com.mycompany.ppai.repositories;

import com.mycompany.ppai.entities.EstacionSismologica;
import jakarta.persistence.EntityManager;
import java.util.List;

public class EstacionSismologicaRepository {
    private static EstacionSismologicaRepository instance = null;
    private final EntityManager entityManager;
        
    private EstacionSismologicaRepository(EntityManager em) {
        this.entityManager = em;
    }

    public static EstacionSismologicaRepository getInstance(EntityManager em) {
        if (instance == null) {
            instance = new EstacionSismologicaRepository(em);
        }
        return instance;
    }

    public List<EstacionSismologica> obtenerTodos() {
        return entityManager.createQuery("SELECT e FROM EstacionSismologica e", EstacionSismologica.class)
                .getResultList();
    }

    public EstacionSismologica guardar(EstacionSismologica estacionSismologica) {
        try {
            entityManager.getTransaction().begin();
            EstacionSismologica mergedEntity = entityManager.merge(estacionSismologica);
            entityManager.getTransaction().commit();
            return mergedEntity;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar la EstacionSismologica", e);
        }
    }
    
    public void guardarTodos(List<EstacionSismologica> listaEstaciones) {
        try {
            entityManager.getTransaction().begin();
            for (EstacionSismologica estacion : listaEstaciones) {
                entityManager.merge(estacion);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar la lista de EstacionesSismologicas", e);
        }
    }
}