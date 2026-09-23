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
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet("/usuarios")
public class UsuarioServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {
            case "perfil":
                mostrarPerfil(request, response);
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

        try {
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
                case "ascenderRolMasivo":
                    ascenderRolMasivo(request, response);
                    break;
                case "eliminarAntiguos":
                    eliminarAntiguos(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/usuarios");
            }
        } catch (ParseException e) {
            request.setAttribute("error", "Formato de fecha inválido. Usa AAAA-MM-DD.");
            doGet(request, response);
        }
    }

    // ---------- LECTURA (GET) ----------

    private void listarTodos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        request.setAttribute("usuarios", usuarios);
        RequestDispatcher rd = request.getRequestDispatcher("/perfil.jsp");
        rd.forward(request, response);
    }

    private void mostrarPerfil(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Usuario usuario = usuarioDAO.buscarPorId(id);
        request.setAttribute("usuario", usuario);
        RequestDispatcher rd = request.getRequestDispatcher("/perfil.jsp");
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
        response.sendRedirect(request.getContextPath() + "/usuarios");
    }

    private void actualizar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Usuario usuario = usuarioDAO.buscarPorId(id);
        if (usuario != null) {
            usuario.setNombre(request.getParameter("nombre"));
            usuario.setEmail(request.getParameter("email"));
            usuarioDAO.actualizar(usuario);
        }
        response.sendRedirect(request.getContextPath() + "/usuarios?accion=perfil&id=" + id);
    }

    private void eliminar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        usuarioDAO.eliminar(id);
        response.sendRedirect(request.getContextPath() + "/usuarios");
    }

    // Operación masiva: asciende de ESTUDIANTE a INSTRUCTOR a los registrados antes de una fecha
    private void ascenderRolMasivo(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ParseException {
        Date fechaLimite = formatoFecha.parse(request.getParameter("fechaLimite"));
        int filas = usuarioDAO.actualizarRolMasivo(RolUsuario.ESTUDIANTE, RolUsuario.INSTRUCTOR, fechaLimite);
        request.getSession().setAttribute("mensaje", filas + " usuario(s) ascendidos.");
        response.sendRedirect(request.getContextPath() + "/usuarios");
    }

    // Operación masiva: elimina usuarios registrados antes de una fecha (ej. cuentas de prueba)
    private void eliminarAntiguos(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ParseException {
        Date fechaLimite = formatoFecha.parse(request.getParameter("fechaLimite"));
        int filas = usuarioDAO.eliminarUsuariosRegistradosAntesDe(fechaLimite);
        request.getSession().setAttribute("mensaje", filas + " usuario(s) eliminados.");
        response.sendRedirect(request.getContextPath() + "/usuarios");
    }
}