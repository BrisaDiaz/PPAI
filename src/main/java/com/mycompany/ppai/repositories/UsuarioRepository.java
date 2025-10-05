package com.mycompany.ppai.repositories;

import com.mycompany.ppai.entities.Usuario;
import jakarta.persistence.EntityManager;
import java.util.List;

public class UsuarioRepository {
    private static UsuarioRepository instance = null;
    private final EntityManager entityManager;
        
    private UsuarioRepository(EntityManager em) {
        this.entityManager = em;
    }

    public static UsuarioRepository getInstance(EntityManager em) {
        if (instance == null) {
            instance = new UsuarioRepository(em);
        }
        return instance;
    }

    public List<Usuario> obtenerTodos() {
        return entityManager.createQuery("SELECT u FROM Usuario u", Usuario.class)
                .getResultList();
    }

    public Usuario guardar(Usuario usuario) {
        try {
            entityManager.getTransaction().begin();
            Usuario mergedUsuario = entityManager.merge(usuario);
            entityManager.getTransaction().commit();
            return mergedUsuario;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar el Usuario", e);
        }
    }
    
    public void guardarTodos(List<Usuario> listaUsuarios) {
        try {
            entityManager.getTransaction().begin();
            for (Usuario usuario : listaUsuarios) {
                entityManager.merge(usuario);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar la lista de Usuarios", e);
        }
    }
}