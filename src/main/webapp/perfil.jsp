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
        <%-- Visibles siempre --%>
        <a href="${pageContext.request.contextPath}/index.jsp">Inicio</a>
        <a href="${pageContext.request.contextPath}/cursos">Cursos</a>

        <%-- Lógica según estado de sesión --%>
        <%
            if (session.getAttribute("usuarioLogueado") != null) {
        %>
        <%-- Sesión activa --%>
        <a href="${pageContext.request.contextPath}/mi-perfil">Mi Perfil</a>
        <a href="${pageContext.request.contextPath}/logout">Cerrar Sesión</a>
        <%
        } else {
        %>
        <%-- Sin sesión --%>
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
    %>
    <h3>Mi Perfil</h3>

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
                <span><%= usuario.getRol() %></span>
            </div>
        </div>
    </div>

    <h3>Mis Cursos Inscritos</h3>

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
        </div>
        <% } %>
    </div>
    <% } else { %>
    <div class="profile-card" style="text-align: center; color: #666;">
        <p>Aún no te has inscrito a ningún curso.</p>
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