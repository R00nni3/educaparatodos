package com.educaparatodos.controller;

import com.educaparatodos.dao.CursoDAO;
import com.educaparatodos.model.Curso;
import com.educaparatodos.model.NivelDificultad;
import com.educaparatodos.model.Usuario;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/cursos")
public class CursoServlet extends HttpServlet {

    private final CursoDAO cursoDAO = new CursoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {
            case "detalle":
                mostrarDetalle(request, response);
                break;
            case "buscar":
                buscarConFiltros(request, response);
                break;
            case "populares":
                mostrarPopulares(request, response);
                break;
            case "nuevo":
                mostrarFormularioNuevo(request, response);
                break;
            case "editar":
                mostrarFormularioEditar(request, response);
                break;
            case "listar":
            default:
                listarTodos(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null) accion = "";

        switch (accion) {
            case "inscribir":
                procesarInscripcion(request, response);
                break;
            case "cancelarInscripcion":
                procesarCancelacionInscripcion(request, response);
                break;
            case "crear":
                crear(request, response);
                break;
            case "actualizar":
                actualizar(request, response);
                break;
            case "eliminar":
                eliminar(request, response);
                break;
            case "actualizarNivelMasivo":
                actualizarNivelMasivo(request, response);
                break;
            case "eliminarPocoPopulares":
                eliminarPocoPopulares(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/cursos");
        }
    }

    // ---------- LECTURA (GET) ----------

    private void listarTodos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Curso> cursos = cursoDAO.listarTodos();
        request.setAttribute("cursos", cursos);
        RequestDispatcher rd = request.getRequestDispatcher("/cursos.jsp");
        rd.forward(request, response);
    }

    private void mostrarDetalle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Curso curso = cursoDAO.buscarPorIdConLecciones(id);
        request.setAttribute("curso", curso);
        RequestDispatcher rd = request.getRequestDispatcher("/curso-detalle.jsp");
        rd.forward(request, response);
    }

    private void buscarConFiltros(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tema = request.getParameter("tema");
        String nivelParam = request.getParameter("nivel");

        List<Curso> resultado;
        if (tema != null && !tema.isEmpty() && nivelParam != null && !nivelParam.isEmpty()) {
            NivelDificultad nivel = NivelDificultad.valueOf(nivelParam);
            resultado = cursoDAO.buscarPorTemaYNivel(tema, nivel);
        } else if (tema != null && !tema.isEmpty()) {
            resultado = cursoDAO.buscarPorTema(tema);
        } else if (nivelParam != null && !nivelParam.isEmpty()) {
            resultado = cursoDAO.buscarPorNivel(NivelDificultad.valueOf(nivelParam));
        } else {
            resultado = cursoDAO.listarTodos();
        }

        request.setAttribute("cursos", resultado);
        request.setAttribute("temaBuscado", tema);
        request.setAttribute("nivelBuscado", nivelParam);
        RequestDispatcher rd = request.getRequestDispatcher("/cursos.jsp");
        rd.forward(request, response);
    }

    private void mostrarPopulares(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Curso> populares = cursoDAO.buscarMasPopulares(10);
        request.setAttribute("cursos", populares);
        RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
        rd.forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        // Solo ADMIN puede acceder a crear un nuevo curso
        if (!esAdmin(usuario)) {
            response.sendRedirect(request.getContextPath() + "/cursos?error=noAutorizado");
            return;
        }

        RequestDispatcher rd = request.getRequestDispatcher("/curso-form.jsp");
        rd.forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        // ADMIN y PROFESOR pueden acceder a editar un curso
        if (!esAdmin(usuario) && !esProfesor(usuario)) {
            response.sendRedirect(request.getContextPath() + "/cursos?error=noAutorizado");
            return;
        }

        Long id = Long.parseLong(request.getParameter("id"));
        Curso curso = cursoDAO.buscarPorId(id);
        request.setAttribute("curso", curso);

        RequestDispatcher rd = request.getRequestDispatcher("/curso-form.jsp");
        rd.forward(request, response);
    }

    // ---------- ESCRITURA (POST) ----------

    private void procesarInscripcion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

            if (usuario == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            Long cursoId = Long.parseLong(request.getParameter("cursoId"));

            boolean exito = cursoDAO.inscribirUsuario(usuario.getId(), cursoId);

            if (exito) {
                response.sendRedirect(request.getContextPath() + "/mi-perfil?inscripcion=exitoso");
            } else {
                response.sendRedirect(request.getContextPath() + "/cursos?accion=detalle&id=" + cursoId + "&error=yaInscrito");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/cursos");
        }
    }

    private void procesarCancelacionInscripcion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

            if (usuario == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            Long cursoId = Long.parseLong(request.getParameter("cursoId"));

            boolean exito = cursoDAO.cancelarInscripcion(usuario.getId(), cursoId);

            if (exito) {
                response.sendRedirect(request.getContextPath() + "/mi-perfil?cancelacion=exitosa");
            } else {
                response.sendRedirect(request.getContextPath() + "/mi-perfil?error=cancelacionFallida");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/mi-perfil");
        }
    }

    private void crear(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        // SOLO ADMIN PUEDE CREAR
        if (!esAdmin(usuario)) {
            response.sendRedirect(request.getContextPath() + "/cursos?error=noAutorizado");
            return;
        }

        Curso curso = new Curso();
        curso.setTitulo(request.getParameter("titulo"));
        curso.setDescripcion(request.getParameter("descripcion"));
        curso.setTema(request.getParameter("tema"));
        curso.setNivel(NivelDificultad.valueOf(request.getParameter("nivel")));
        curso.setPopularidad(0);

        cursoDAO.crear(curso);
        response.sendRedirect(request.getContextPath() + "/cursos");
    }

    private void actualizar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        // ADMIN Y PROFESOR PUEDEN EDITAR
        if (!esAdmin(usuario) && !esProfesor(usuario)) {
            response.sendRedirect(request.getContextPath() + "/cursos?error=noAutorizado");
            return;
        }

        Long id = Long.parseLong(request.getParameter("id"));
        Curso curso = cursoDAO.buscarPorId(id);
        if (curso != null) {
            curso.setTitulo(request.getParameter("titulo"));
            curso.setDescripcion(request.getParameter("descripcion"));
            curso.setTema(request.getParameter("tema"));
            curso.setNivel(NivelDificultad.valueOf(request.getParameter("nivel")));
            cursoDAO.actualizar(curso);
        }
        response.sendRedirect(request.getContextPath() + "/cursos?accion=detalle&id=" + id);
    }

    private void eliminar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        // SOLO ADMIN PUEDE ELIMINAR
        if (!esAdmin(usuario)) {
            response.sendRedirect(request.getContextPath() + "/cursos?error=noAutorizado");
            return;
        }

        Long id = Long.parseLong(request.getParameter("id"));
        cursoDAO.eliminar(id);
        response.sendRedirect(request.getContextPath() + "/cursos");
    }

    // Operación masiva: sube el nivel de todos los cursos de un tema (Solo ADMIN)
    private void actualizarNivelMasivo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        if (!esAdmin(usuario)) {
            response.sendRedirect(request.getContextPath() + "/cursos?error=noAutorizado");
            return;
        }

        String tema = request.getParameter("tema");
        NivelDificultad nuevoNivel = NivelDificultad.valueOf(request.getParameter("nuevoNivel"));
        int filas = cursoDAO.actualizarNivelPorTema(tema, nuevoNivel);
        request.getSession().setAttribute("mensaje", filas + " curso(s) actualizados.");
        response.sendRedirect(request.getContextPath() + "/cursos");
    }

    // Operación masiva: elimina cursos con poca popularidad (Solo ADMIN)
    private void eliminarPocoPopulares(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        if (!esAdmin(usuario)) {
            response.sendRedirect(request.getContextPath() + "/cursos?error=noAutorizado");
            return;
        }

        int umbral = Integer.parseInt(request.getParameter("umbral"));
        int filas = cursoDAO.eliminarCursosPocoPopulares(umbral);
        request.getSession().setAttribute("mensaje", filas + " curso(s) eliminados.");
        response.sendRedirect(request.getContextPath() + "/cursos");
    }

    // ---------- MÉTODOS AUXILIARES DE VERIFICACIÓN DE ROL ----------

    private boolean esAdmin(Usuario u) {
        return u != null && u.getRol() != null && u.getRol().toString().equalsIgnoreCase("ADMIN");
    }

    private boolean esProfesor(Usuario u) {
        if (u == null || u.getRol() == null) return false;
        String rol = u.getRol().toString();
        return "PROFESOR".equalsIgnoreCase(rol) || "INSTRUCTOR".equalsIgnoreCase(rol);
    }
}