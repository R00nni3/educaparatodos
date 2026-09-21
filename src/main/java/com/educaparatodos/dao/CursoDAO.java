package com.educaparatodos.dao;

import com.educaparatodos.model.Curso;
import com.educaparatodos.model.NivelDificultad;
import com.educaparatodos.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;

public class CursoDAO {

    private final EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();

    // ---------- CRUD BÁSICO ----------

    public Curso crear(Curso curso) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(curso);
            tx.commit();
            return curso;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Curso buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Curso.class, id);
        } finally {
            em.close();
        }
    }

    public List<Curso> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Curso> query = em.createQuery(
                    "SELECT c FROM Curso c ORDER BY c.titulo ASC", Curso.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Curso actualizar(Curso curso) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Curso actualizado = em.merge(curso);
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
            Curso curso = em.find(Curso.class, id);
            if (curso != null) {
                em.remove(curso);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // ---------- CONSULTAS JPQL (búsqueda por tema, nivel, popularidad) ----------

    public List<Curso> buscarPorTema(String tema) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Curso> query = em.createQuery(
                    "SELECT c FROM Curso c WHERE LOWER(c.tema) LIKE LOWER(CONCAT('%', :tema, '%'))",
                    Curso.class);
            query.setParameter("tema", tema);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Curso> buscarPorNivel(NivelDificultad nivel) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Curso> query = em.createQuery(
                    "SELECT c FROM Curso c WHERE c.nivel = :nivel", Curso.class);
            query.setParameter("nivel", nivel);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Los "n" cursos más populares (ej: top 10 para la página de inicio)
    public List<Curso> buscarMasPopulares(int limite) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Curso> query = em.createQuery(
                    "SELECT c FROM Curso c ORDER BY c.popularidad DESC", Curso.class);
            query.setMaxResults(limite);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Consulta compleja: cursos de un tema Y nivel específico, ordenados por popularidad
    public List<Curso> buscarPorTemaYNivel(String tema, NivelDificultad nivel) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Curso> query = em.createQuery(
                    "SELECT c FROM Curso c " +
                            "WHERE LOWER(c.tema) LIKE LOWER(CONCAT('%', :tema, '%')) " +
                            "AND c.nivel = :nivel " +
                            "ORDER BY c.popularidad DESC",
                    Curso.class);
            query.setParameter("tema", tema);
            query.setParameter("nivel", nivel);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // ---------- OPERACIONES MASIVAS (UPDATE / DELETE) ----------

    /**
     * UPDATE masivo: sube el nivel de dificultad de todos los cursos de un tema.
     * executeUpdate() modifica DIRECTAMENTE en la base de datos, sin cargar
     * las entidades en memoria una por una (mucho más eficiente para lotes grandes).
     * @return cantidad de filas afectadas
     */
    public int actualizarNivelPorTema(String tema, NivelDificultad nuevoNivel) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            int filasAfectadas = em.createQuery(
                            "UPDATE Curso c SET c.nivel = :nuevoNivel WHERE LOWER(c.tema) = LOWER(:tema)")
                    .setParameter("nuevoNivel", nuevoNivel)
                    .setParameter("tema", tema)
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
     * DELETE masivo: elimina todos los cursos con popularidad por debajo
     * de un umbral (ej. limpiar cursos que nadie toma).
     * @return cantidad de filas eliminadas
     */
    public int eliminarCursosPocoPopulares(int umbralPopularidad) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            int filasEliminadas = em.createQuery(
                            "DELETE FROM Curso c WHERE c.popularidad < :umbral")
                    .setParameter("umbral", umbralPopularidad)
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