package com.educaparatodos.dao;

import com.educaparatodos.model.RolUsuario;
import com.educaparatodos.model.Usuario;
import com.educaparatodos.util.JPAUtil;
import com.educaparatodos.util.PasswordUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

public class UsuarioDAO {

    private final EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();

    // ---------- CRUD BÁSICO ----------

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
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email AND u.password = :password", Usuario.class)
                    .setParameter("email", email)
                    .setParameter("password", passwordHasheada)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
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

    public Usuario actualizar(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Usuario actualizado = em.merge(usuario);
            tx.commit();
            return actualizado;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
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

    // ---------- CONSULTAS JPQL ----------

    public List<Usuario> buscarPorRol(RolUsuario rol) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.rol = :rol", Usuario.class);
            query.setParameter("rol", rol);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // ---------- OPERACIONES MASIVAS (UPDATE / DELETE) ----------

    /**
     * UPDATE masivo: asciende de rol a todos los estudiantes registrados
     * antes de una fecha dada (ej. usuarios "veteranos" pasan a instructor).
     * @return cantidad de filas afectadas
     */
    public int actualizarRolMasivo(RolUsuario rolActual, RolUsuario nuevoRol, Date registradosAntesDe) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            int filasAfectadas = em.createQuery(
                            "UPDATE Usuario u SET u.rol = :nuevoRol " +
                                    "WHERE u.rol = :rolActual AND u.fechaRegistro < :fecha")
                    .setParameter("nuevoRol", nuevoRol)
                    .setParameter("rolActual", rolActual)
                    .setParameter("fecha", registradosAntesDe)
                    .executeUpdate();
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
     * DELETE masivo: elimina usuarios registrados antes de una fecha dada
     * (ej. limpiar cuentas de prueba antiguas nunca activadas).
     * @return cantidad de filas eliminadas
     */
    public int eliminarUsuariosRegistradosAntesDe(Date fecha) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            int filasEliminadas = em.createQuery(
                            "DELETE FROM Usuario u WHERE u.fechaRegistro < :fecha")
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