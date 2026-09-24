package com.educaparatodos.dao;

import com.educaparatodos.model.RolUsuario;
import com.educaparatodos.model.Usuario;
import com.educaparatodos.util.JPAUtil;
import com.educaparatodos.util.PasswordUtil;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

public class UsuarioDAO {

    private final EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();

    // ---------- CRUD BÁSICO Y AUTENTICACIÓN ----------

    public Usuario crear(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(usuario);
            tx.commit();
            return usuario;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Usuario autenticar(String email, String passwordPlana) {
        String passwordHasheada = PasswordUtil.hashearPassword(passwordPlana);
        EntityManager em = emf.createEntityManager();
        try {
            List<Usuario> resultados = em.createQuery(
                            "SELECT u FROM Usuario u WHERE u.email = :email AND u.password = :password", Usuario.class)
                    .setParameter("email", email)
                    .setParameter("password", passwordHasheada)
                    .getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } finally {
            em.close();
        }
    }

    public Usuario buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public Usuario buscarPorEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class);
            query.setParameter("email", email);
            List<Usuario> resultado = query.getResultList();
            return resultado.isEmpty() ? null : resultado.get(0);
        } finally {
            em.close();
        }
    }

    public List<Usuario> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u ORDER BY u.nombre ASC", Usuario.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Usuario usuario = em.find(Usuario.class, id);
            if (usuario != null) {
                em.remove(usuario);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // ---------- ACCIONES PUNTUALES ----------

    /**
     * Cambia el rol de un usuario específico a partir de su ID.
     */
    public boolean cambiarRolUsuario(Long usuarioId, String nuevoRol) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Usuario usuario = em.find(Usuario.class, usuarioId);
            if (usuario != null) {
                RolUsuario rolEnum = null;

                for (RolUsuario r : RolUsuario.values()) {
                    if (r.name().equalsIgnoreCase(nuevoRol) ||
                            (nuevoRol.equalsIgnoreCase("PROFESOR") && r.name().equalsIgnoreCase("INSTRUCTOR")) ||
                            (nuevoRol.equalsIgnoreCase("INSTRUCTOR") && r.name().equalsIgnoreCase("PROFESOR"))) {
                        rolEnum = r;
                        break;
                    }
                }

                if (rolEnum != null) {
                    usuario.setRol(rolEnum);
                    tx.commit();
                    return true;
                }
            }
            return false;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // ---------- OPERACIONES MASIVAS (UPDATE / DELETE) ----------

    /**
     * UPDATE masivo: asciende a INSTRUCTOR a todos los estudiantes cuyo email
     * pertenezca a CUALQUIERA de los dominios indicados.
     * Insensible a mayúsculas/minúsculas y adaptable si no se incluye el '.com' o se agrega un '@'.
     */
    public int ascenderPorDominios(List<String> dominios) {
        if (dominios == null || dominios.isEmpty()) {
            return 0;
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            StringBuilder jpql = new StringBuilder(
                    "UPDATE Usuario u SET u.rol = :nuevoRol WHERE u.rol = :rolActual AND (");
            for (int i = 0; i < dominios.size(); i++) {
                if (i > 0) jpql.append(" OR ");
                jpql.append("LOWER(u.email) LIKE LOWER(:dominio").append(i).append(")");
            }
            jpql.append(")");

            Query query = em.createQuery(jpql.toString());
            query.setParameter("nuevoRol", RolUsuario.INSTRUCTOR);
            query.setParameter("rolActual", RolUsuario.ESTUDIANTE);

            for (int i = 0; i < dominios.size(); i++) {
                String d = dominios.get(i).trim().toLowerCase();
                if (d.startsWith("@")) {
                    d = d.substring(1);
                }
                if (!d.contains(".")) {
                    query.setParameter("dominio" + i, "%@" + d + "%");
                } else {
                    query.setParameter("dominio" + i, "%@" + d);
                }
            }

            int filasAfectadas = query.executeUpdate();
            tx.commit();
            return filasAfectadas;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * DELETE masivo: elimina estudiantes que NO tienen ninguna inscripción
     * Y que se registraron antes de la fecha indicada.
     */
    public int eliminarEstudiantesSinInscripcionAntesDe(Date fecha) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            int filasEliminadas = em.createQuery(
                            "DELETE FROM Usuario u WHERE u.rol = :rol " +
                                    "AND u.fechaRegistro < :fecha " +
                                    "AND u.id NOT IN (SELECT DISTINCT i.usuario.id FROM Inscripcion i)")
                    .setParameter("rol", RolUsuario.ESTUDIANTE)
                    .setParameter("fecha", fecha)
                    .executeUpdate();
            tx.commit();
            return filasEliminadas;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}