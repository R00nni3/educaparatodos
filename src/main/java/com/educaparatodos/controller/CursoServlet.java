package com.educaparatodos.controller;

import com.educaparatodos.dao.CursoDAO;
import com.educaparatodos.model.Curso;
import com.educaparatodos.model.NivelDificultad;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
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
        Curso curso = cursoDAO.buscarPorId(id);
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

    // ---------- ESCRITURA (POST) ----------

    private void crear(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        Long id = Long.parseLong(request.getParameter("id"));
        cursoDAO.eliminar(id);
        response.sendRedirect(request.getContextPath() + "/cursos");
    }

    // Operación masiva: sube el nivel de todos los cursos de un tema
    private void actualizarNivelMasivo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tema = request.getParameter("tema");
        NivelDificultad nuevoNivel = NivelDificultad.valueOf(request.getParameter("nuevoNivel"));
        int filas = cursoDAO.actualizarNivelPorTema(tema, nuevoNivel);
        request.getSession().setAttribute("mensaje", filas + " curso(s) actualizados.");
        response.sendRedirect(request.getContextPath() + "/cursos");
    }

    // Operación masiva: elimina cursos con poca popularidad
    private void eliminarPocoPopulares(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int umbral = Integer.parseInt(request.getParameter("umbral"));
        int filas = cursoDAO.eliminarCursosPocoPopulares(umbral);
        request.getSession().setAttribute("mensaje", filas + " curso(s) eliminados.");
        response.sendRedirect(request.getContextPath() + "/cursos");
    }
}