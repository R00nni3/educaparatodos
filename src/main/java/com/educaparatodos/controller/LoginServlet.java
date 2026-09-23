package com.educaparatodos.controller;

import com.educaparatodos.dao.UsuarioDAO;
import com.educaparatodos.model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si el usuario intenta acceder a /login por URL, le mostramos el formulario
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validar con la base de datos
        Usuario usuario = usuarioDAO.autenticar(email, password);

        if (usuario != null) {
            // Credenciales correctas: Crear sesión
            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogueado", usuario);

            // Redirigir a la lista de cursos
            response.sendRedirect(request.getContextPath() + "/mi-perfil");
        } else {
            // Credenciales incorrectas: Volver al login con error
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=1");
        }
    }
}