package com.mycompany.ppai.repositories;

import com.mycompany.ppai.entities.MotivoTipo;
import jakarta.persistence.EntityManager;
import java.util.List;

public class MotivoTipoRespository {
    private static MotivoTipoRespository instance = null;
    private final EntityManager entityManager;
        
    private MotivoTipoRespository(EntityManager em) {
        this.entityManager = em;
    }

    public static MotivoTipoRespository getInstance(EntityManager em) {
        if (instance == null) {
            instance = new MotivoTipoRespository(em);
        }
        return instance;
    }

    public List<MotivoTipo> obtenerTodos() {
        return entityManager.createQuery("SELECT m FROM MotivoTipo m", MotivoTipo.class)
                .getResultList();
    }

    public MotivoTipo guardar(MotivoTipo motivoTipo) {
        try {

            entityManager.getTransaction().begin();
            MotivoTipo mergedMotivo = entityManager.merge(motivoTipo);

            entityManager.getTransaction().commit();
            return mergedMotivo;
        } catch (Exception e) {

            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar el MotivoTipo", e);
        }
    }

    public void guardarTodos(List<MotivoTipo> listaMotivoTipos) {
        try {

            entityManager.getTransaction().begin();
            for (MotivoTipo motivoTipo : listaMotivoTipos) {
                entityManager.merge(motivoTipo);
            }

            entityManager.getTransaction().commit();
        } catch (Exception e) {

            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar la lista de MotivoTipo", e);
        }
    }

    public MotivoTipo obtenerPorDescripcion(String descripcion) {
        try {
            return entityManager.createQuery(
                    "SELECT m FROM MotivoTipo m WHERE LOWER(m.descripcion) = LOWER(:desc)", MotivoTipo.class)
                    .setParameter("desc", descripcion)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }
}
