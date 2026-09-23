package com.educaparatodos.controller;

import com.educaparatodos.dao.CursoDAO;
import com.educaparatodos.model.Curso;
import com.educaparatodos.model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/mi-perfil")
public class PerfilServlet extends HttpServlet {

    private final CursoDAO cursoDAO = new CursoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario != null) {
            // 1. Buscamos los cursos usando el ID del usuario
            List<Curso> misCursos = cursoDAO.obtenerCursosPorUsuario(usuario.getId());

            // 2. Guardamos la lista en la petición para que el JSP pueda leerla
            request.setAttribute("misCursos", misCursos);

            // 3. Enviamos al usuario a la vista
            request.getRequestDispatcher("/perfil.jsp").forward(request, response);
        } else {
            // Si no hay sesión, al login
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}