package com.educaparatodos.dao;

import com.educaparatodos.model.Curso;
import com.educaparatodos.model.Inscripcion;
import com.educaparatodos.model.NivelDificultad;
import com.educaparatodos.model.Usuario;
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

    /**
     * Busca un curso por id, cargando también sus lecciones EN LA MISMA CONSULTA
     * (JOIN FETCH), para poder mostrarlas en curso-detalle.jsp sin que explote
     * la conexión ya cerrada (LazyInitializationException).
     */
    public Curso buscarPorIdConLecciones(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Curso> query = em.createQuery(
                    "SELECT DISTINCT c FROM Curso c " +
                            "LEFT JOIN FETCH c.lecciones " +
                            "WHERE c.id = :id",
                    Curso.class);
            query.setParameter("id", id);
            List<Curso> resultado = query.getResultList();
            return resultado.isEmpty() ? null : resultado.get(0);
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

    // ---------- INSCRIPCIÓN DE USUARIOS ----------

    /**
     * Inscribe a un usuario en un curso creando un registro en la entidad de Inscripcion.
     * Retorna true si se registró con éxito, o false si ya estaba inscrito o hubo un error.
     */
    public boolean inscribirUsuario(Long usuarioId, Long cursoId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Usuario usuario = em.find(Usuario.class, usuarioId);
            Curso curso = em.find(Curso.class, cursoId);

            if (usuario == null || curso == null) {
                return false;
            }

            // 1. Verificamos si ya existe una inscripción previa entre este usuario y este curso
            Long count = em.createQuery(
                            "SELECT COUNT(i) FROM Inscripcion i WHERE i.usuario.id = :usuarioId AND i.curso.id = :cursoId",
                            Long.class)
                    .setParameter("usuarioId", usuarioId)
                    .setParameter("cursoId", cursoId)
                    .getSingleResult();

            if (count > 0) {
                return false; // Ya está inscrito previamente
            }

            // 2. Creamos la nueva inscripción
            Inscripcion nuevaInscripcion = new Inscripcion();
            nuevaInscripcion.setUsuario(usuario);
            nuevaInscripcion.setCurso(curso);

            em.persist(nuevaInscripcion);

            // Incrementamos la popularidad del curso al registrar una inscripción
            curso.setPopularidad(curso.getPopularidad() + 1);
            em.merge(curso);

            tx.commit();
            return true;

        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean estaInscrito(Long usuarioId, Long cursoId) {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(i) FROM Inscripcion i WHERE i.usuario.id = :usuarioId AND i.curso.id = :cursoId",
                            Long.class)
                    .setParameter("usuarioId", usuarioId)
                    .setParameter("cursoId", cursoId)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    // -- Cancelar subscripción --

    public boolean cancelarInscripcion(Long usuarioId, Long cursoId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // 1. Buscamos la inscripción correspondiente
            TypedQuery<Inscripcion> query = em.createQuery(
                    "SELECT i FROM Inscripcion i WHERE i.usuario.id = :usuarioId AND i.curso.id = :cursoId",
                    Inscripcion.class);
            query.setParameter("usuarioId", usuarioId);
            query.setParameter("cursoId", cursoId);

            List<Inscripcion> resultados = query.getResultList();

            if (resultados.isEmpty()) {
                return false; // No se encontró la inscripción
            }

            Inscripcion inscripcion = resultados.get(0);
            Curso curso = inscripcion.getCurso();

            // 2. Eliminamos la inscripción de la base de datos
            em.remove(inscripcion);

            // 3. Decrementamos la popularidad del curso (sin bajar de 0)
            if (curso != null && curso.getPopularidad() > 0) {
                curso.setPopularidad(curso.getPopularidad() - 1);
                em.merge(curso);
            }

            tx.commit();
            return true;

        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
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
    public List<Curso> obtenerCursosPorUsuario(Long usuarioId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                            "SELECT i.curso FROM Inscripcion i WHERE i.usuario.id = :usuarioId", Curso.class)
                    .setParameter("usuarioId", usuarioId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}