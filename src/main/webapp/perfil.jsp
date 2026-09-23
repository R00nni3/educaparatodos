<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.educaparatodos.model.Curso" %>
<%@ page import="com.educaparatodos.model.Usuario" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mi Perfil - EducaParaTodos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<header class="main-header">
    <h1><a href="${pageContext.request.contextPath}/index.jsp" style="color: white; text-decoration: none;">EducaParaTodos</a></h1>
    <nav>
        <a href="${pageContext.request.contextPath}/index.jsp">Inicio</a>
        <a href="${pageContext.request.contextPath}/cursos">Cursos</a>

        <%-- Lógica según estado de sesión --%>
        <%
            Usuario uLog = (Usuario) session.getAttribute("usuarioLogueado");
            if (uLog != null) {
                String rolStr = uLog.getRol() != null ? uLog.getRol().toString().toUpperCase() : "";
                boolean esAdmin = "ADMIN".equals(rolStr);
        %>
        <a href="${pageContext.request.contextPath}/mi-perfil">Mi Perfil</a>
        <% if (esAdmin) { %>
        <a href="${pageContext.request.contextPath}/usuarios?accion=listarAdmin" style="color: #ffd700; font-weight: bold;">Gestión Usuarios</a>
        <% } %>
        <a href="${pageContext.request.contextPath}/logout">Cerrar Sesión</a>
        <%
        } else {
        %>
        <a href="${pageContext.request.contextPath}/login.jsp">Iniciar Sesión</a>
        <a href="${pageContext.request.contextPath}/registro.jsp">Registrarse</a>
        <%
            }
        %>
    </nav>
</header>

<div class="main-content container">
    <%
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            String rol = usuario.getRol() != null ? usuario.getRol().toString().toUpperCase() : "";
            boolean esAdmin = "ADMIN".equals(rol);
            boolean esProfesor = "PROFESOR".equals(rol) || "INSTRUCTOR".equals(rol);
    %>
    <h3>Mi Perfil</h3>

    <%-- ALERTAS DE ACCIÓN --%>
    <% if ("exitosa".equals(request.getParameter("cancelacion"))) { %>
    <div class="alert-message alert-success" style="margin-bottom: 1.5rem;">
        Has anulado tu inscripción al curso correctamente.
    </div>
    <% } else if ("cancelacionFallida".equals(request.getParameter("error"))) { %>
    <div class="alert-message alert-error" style="margin-bottom: 1.5rem;">
        No se pudo anular la inscripción al curso.
    </div>
    <% } %>

    <div class="profile-card">
        <div class="profile-info-grid">
            <div class="profile-info-item">
                <label>Nombre</label>
                <span><%= usuario.getNombre() %></span>
            </div>
            <div class="profile-info-item">
                <label>Email</label>
                <span><%= usuario.getEmail() %></span>
            </div>
            <div class="profile-info-item">
                <label>Rol</label>
                <span><strong><%= usuario.getRol() %></strong></span>
            </div>
        </div>

        <%-- ACCIONES DE ADMINISTRADOR --%>
        <% if (esAdmin) { %>
        <div style="margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid #eee;">
            <a href="${pageContext.request.contextPath}/usuarios?accion=listarAdmin" class="btn-details" style="background-color: #2563eb; color: white; text-decoration: none; display: inline-block;">
                ⚙️ Administrar Usuarios y Roles
            </a>
        </div>
        <% } %>
    </div>

    <h3>Mis Cursos</h3>

    <%
        List<Curso> misCursos = (List<Curso>) request.getAttribute("misCursos");
        if (misCursos != null && !misCursos.isEmpty()) {
    %>
    <div class="courses-grid">
        <% for (Curso c : misCursos) { %>
        <div class="course-card">
            <div>
                <span class="badge"><%= c.getNivel() %></span>
                <h4><%= c.getTitulo() %></h4>
                <p><%= c.getDescripcion() != null ? c.getDescripcion() : "Sin descripción disponible." %></p>
            </div>
            <small style="color: #777;">Tema: <%= c.getTema() %></small>

            <div style="margin-top: 1rem; display: flex; flex-direction: column; gap: 8px;">
                <a href="${pageContext.request.contextPath}/cursos?accion=detalle&id=<%= c.getId() %>" class="btn-details" style="text-align: center; text-decoration: none;">
                    Ver Detalle
                </a>

                <%-- BOTÓN DE EDICIÓN VISIBLE PARA PROFESOR/INSTRUCTOR Y ADMIN --%>
                <% if (esProfesor || esAdmin) { %>
                <a href="${pageContext.request.contextPath}/cursos?accion=editar&id=<%= c.getId() %>" class="btn-details" style="background-color: #d97706; text-align: center; text-decoration: none;">
                    ✏️ Editar Curso
                </a>
                <% } %>

                <form action="${pageContext.request.contextPath}/cursos" method="post" onsubmit="return confirm('¿Estás seguro/a de que deseas anular tu inscripción a este curso?');" style="margin: 0;">
                    <input type="hidden" name="accion" value="cancelarInscripcion">
                    <input type="hidden" name="cursoId" value="<%= c.getId() %>">
                    <button type="submit" class="btn-details" style="background-color: #a83232; color: white; border: none; padding: 0.5rem 1rem; cursor: pointer; border-radius: 6px; width: 100%;">
                        Anular Inscripción
                    </button>
                </form>
            </div>
        </div>
        <% } %>
    </div>
    <% } else { %>
    <div class="profile-card" style="text-align: center; color: #666;">
        <p>Aún no tienes cursos asignados ni inscritos.</p>
        <a href="${pageContext.request.contextPath}/cursos" class="btn-details" style="display: inline-block; margin-top: 10px;">Explorar Catálogo</a>
    </div>
    <% } %>

    <% } else { %>
    <div class="auth-container">
        <div class="auth-card" style="text-align: center;">
            <h2>Acceso Restringido</h2>
            <p style="color: #666;">Debes iniciar sesión para ver tu perfil.</p>
            <a href="${pageContext.request.contextPath}/login.jsp" class="btn-details btn-block" style="text-decoration: none;">Ir a Iniciar Sesión</a>
        </div>
    </div>
    <% } %>
</div>

<footer>
    <p>&copy; 2026 EducaParaTodos. Todos los derechos reservados.</p>
</footer>
</body>
</html>