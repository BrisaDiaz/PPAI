package com.mycompany.ppai.repositories;

import com.mycompany.ppai.entities.Sismografo;
import com.mycompany.ppai.entities.Sismografo;
import jakarta.persistence.EntityManager;
import java.util.List;

public class SismografoRepository {
    private static SismografoRepository instance = null;
    private final EntityManager entityManager;
        
    private SismografoRepository(EntityManager em) {
        this.entityManager = em;
    }

    public static SismografoRepository getInstance(EntityManager em) {
        if (instance == null) {
            instance = new SismografoRepository(em);
        }
        return instance;
    }

    public List<Sismografo> obtenerTodos() {
        return entityManager.createQuery("SELECT s FROM Sismografo s", Sismografo.class)
                .getResultList();
    }

    public Sismografo guardar(Sismografo sismografo) {
        try {
            entityManager.getTransaction().begin();
            Sismografo mergedOrden = entityManager.merge(sismografo);
            entityManager.getTransaction().commit();
            return mergedOrden;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar el Sismografo", e);
        }
    }

    public void guardarTodos(List<Sismografo> listaSismografos) {
        try {
            entityManager.getTransaction().begin();
            for (Sismografo orden : listaSismografos) {
                entityManager.merge(orden);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar la lista de Sismografo", e);
        }
    }
}