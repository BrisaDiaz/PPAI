package com.mycompany.ppai.repositories;

import com.mycompany.ppai.entities.OrdenDeInspeccion;
import jakarta.persistence.EntityManager;
import java.util.List;

public class OrdenDeInspeccionRepository {
    private static OrdenDeInspeccionRepository instance = null;
    private final EntityManager entityManager;
        
    private OrdenDeInspeccionRepository(EntityManager em) {
        this.entityManager = em;
    }

    public static OrdenDeInspeccionRepository getInstance(EntityManager em) {
        if (instance == null) {
            instance = new OrdenDeInspeccionRepository(em);
        }
        return instance;
    }

    public List<OrdenDeInspeccion> obtenerTodos() {
        return entityManager.createQuery("SELECT o FROM OrdenDeInspeccion o", OrdenDeInspeccion.class)
                .getResultList();
    }

     public OrdenDeInspeccion guardar(OrdenDeInspeccion ordenDeInspeccion) {
        try {
            entityManager.getTransaction().begin();
            OrdenDeInspeccion mergedOrden = entityManager.merge(ordenDeInspeccion);
            entityManager.getTransaction().commit();
            return mergedOrden;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar la OrdenDeInspeccion", e);
        }
    }

    public void guardarTodos(List<OrdenDeInspeccion> listaOrdenDeInspeccions) {
        try {
            entityManager.getTransaction().begin();
            for (OrdenDeInspeccion orden : listaOrdenDeInspeccions) {
                entityManager.merge(orden);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar la lista de OrdenesDeInspeccion", e);
        }
    }

    public OrdenDeInspeccion obtenerPorNumero(Integer numeroOrden) {
        try {
            return entityManager.createQuery(
                    "SELECT o FROM OrdenDeInspeccion o WHERE o.numeroOrden = :numero", OrdenDeInspeccion.class)
                    .setParameter("numero", numeroOrden)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }
}
