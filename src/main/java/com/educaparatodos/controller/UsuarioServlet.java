package com.educaparatodos.controller;

import com.educaparatodos.dao.UsuarioDAO;
import com.educaparatodos.model.RolUsuario;
import com.educaparatodos.model.Usuario;
import com.educaparatodos.util.PasswordUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet("/usuarios")
public class UsuarioServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listarAdmin";

        switch (accion) {
            case "listarAdmin":
                listarParaAdmin(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/index.jsp");
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
            case "ascenderPorDominio":
                ascenderPorDominio(request, response);
                break;
            case "eliminarSinInscripcion":
                eliminarSinInscripcion(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/usuarios?accion=listarAdmin");
                break;
        }
    }

    // ---------- LECTURA (GET) ----------

    private void listarParaAdmin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuarioLogueado = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        // Solo administradores pueden ver el panel de gestión de usuarios
        if (!esAdmin(usuarioLogueado)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        List<Usuario> usuarios = usuarioDAO.listarTodos();
        request.setAttribute("usuarios", usuarios);
        RequestDispatcher rd = request.getRequestDispatcher("/admin-usuarios.jsp");
        rd.forward(request, response);
    }

    // ---------- ESCRITURA (POST) ----------

    private void crear(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getParameter("nombre"));
        usuario.setEmail(request.getParameter("email"));
        String passwordSegura = PasswordUtil.hashearPassword(request.getParameter("password"));
        usuario.setPassword(passwordSegura);
        String rolParam = request.getParameter("rol");
        usuario.setRol(rolParam != null && !rolParam.isEmpty()
                ? RolUsuario.valueOf(rolParam)
                : RolUsuario.ESTUDIANTE);

        usuarioDAO.crear(usuario);
        response.sendRedirect(request.getContextPath() + "/usuarios?accion=listarAdmin");
    }

    private void actualizar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuarioLogueado = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        if (!esAdmin(usuarioLogueado)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        Long id = Long.parseLong(request.getParameter("id"));
        String rolParam = request.getParameter("rol");

        if (rolParam != null && !rolParam.isEmpty()) {
            boolean exito = usuarioDAO.cambiarRolUsuario(id, rolParam);
            if (exito) {
                request.getSession().setAttribute("mensaje", "Rol de usuario actualizado correctamente.");
            } else {
                request.getSession().setAttribute("mensaje", "No se pudo actualizar el rol del usuario.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/usuarios?accion=listarAdmin");
    }

    private void eliminar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuarioLogueado = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        if (!esAdmin(usuarioLogueado)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        Long id = Long.parseLong(request.getParameter("id"));

        // Evita que un admin se elimine a sí mismo por accidente
        if (usuarioLogueado.getId().equals(id)) {
            request.getSession().setAttribute("mensaje", "No puedes eliminar tu propia cuenta mientras estás conectado.");
            response.sendRedirect(request.getContextPath() + "/usuarios?accion=listarAdmin");
            return;
        }

        usuarioDAO.eliminar(id);
        request.getSession().setAttribute("mensaje", "Usuario eliminado correctamente.");
        response.sendRedirect(request.getContextPath() + "/usuarios?accion=listarAdmin");
    }

    // ---------- OPERACIONES MASIVAS ----------

    private void ascenderPorDominio(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuarioLogueado = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        if (!esAdmin(usuarioLogueado)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String dominiosParam = request.getParameter("dominios");
        List<String> dominios = new ArrayList<>();
        if (dominiosParam != null && !dominiosParam.isEmpty()) {
            for (String d : dominiosParam.split(",")) {
                if (!d.trim().isEmpty()) {
                    dominios.add(d.trim());
                }
            }
        }

        int filas = usuarioDAO.ascenderPorDominios(dominios);
        request.getSession().setAttribute("mensaje", filas + " estudiante(s) ascendido(s) a INSTRUCTOR.");
        response.sendRedirect(request.getContextPath() + "/usuarios?accion=listarAdmin");
    }

    private void eliminarSinInscripcion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuarioLogueado = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;

        if (!esAdmin(usuarioLogueado)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaLimite = formatoFecha.parse(request.getParameter("fechaLimite"));

            int filas = usuarioDAO.eliminarEstudiantesSinInscripcionAntesDe(fechaLimite);
            request.getSession().setAttribute("mensaje", filas + " estudiante(s) sin inscripción eliminado(s).");
        } catch (java.text.ParseException e) {
            request.getSession().setAttribute("mensaje", "Fecha inválida, usa el formato AAAA-MM-DD.");
        }

        response.sendRedirect(request.getContextPath() + "/usuarios?accion=listarAdmin");
    }

    // ---------- MÉTODOS AUXILIARES ----------

    private boolean esAdmin(Usuario u) {
        return u != null && u.getRol() != null && u.getRol().toString().equalsIgnoreCase("ADMIN");
    }
}