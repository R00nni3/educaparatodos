<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.educaparatodos.model.Curso" %>
<%@ page import="com.educaparatodos.dao.CursoDAO" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // Obtener los 3 cursos destacados (o 3 al azar si todos tienen 0 inscripciones)
    CursoDAO cursoDAO = new CursoDAO();
    List<Curso> cursosDestacados = cursoDAO.buscarMasPopulares(3);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>EducaParaTodos - Inicio</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<!-- Encabezado -->
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

<!-- Contenedor principal que empuja el footer hacia abajo -->
<div class="main-content">
    <!-- Hero -->
    <section class="hero">
        <div class="hero-content">
            <h2>Bienvenido a la plataforma educativa</h2>
            <p>Ofrecemos cursos gratuitos en una variedad de temas para comunidades desfavorecidas. ¡Aprende a tu propio ritmo!</p>
            <a href="${pageContext.request.contextPath}/cursos" class="btn-primary">Explorar Cursos</a>
        </div>
    </section>

    <!-- Sección de Cursos Destacados -->
    <section class="container" style="margin-top: 3rem; margin-bottom: 3rem;">
        <h3 style="text-align: center; color: #4a2d40; margin-bottom: 2rem;">Cursos Destacados</h3>

        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 20px;">
            <%
                if (cursosDestacados != null && !cursosDestacados.isEmpty()) {
                    for (Curso c : cursosDestacados) {
            %>
            <div style="background: white; border-radius: 12px; padding: 1.5rem; box-shadow: 0 4px 10px rgba(0,0,0,0.08); display: flex; flex-direction: column; justify-content: space-between;">
                <div>
                    <h4 style="margin-top: 0; color: #5c3043; font-size: 1.2rem;"><%= c.getTitulo() %></h4>
                    <p style="color: #666; font-size: 0.9rem; line-height: 1.4; margin-bottom: 1rem;"><%= c.getDescripcion() %></p>
                    <span style="display: inline-block; background: #f3e8ee; color: #5c3043; padding: 4px 10px; border-radius: 6px; font-size: 0.8rem; font-weight: bold;">
                            Nivel: <%= c.getNivel() %>
                        </span>
                </div>

                <div style="margin-top: 1.5rem; display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #f0f0f0; padding-top: 10px;">
                    <small style="color: #888; font-weight: 500;">👥 <%= c.getPopularidad() %> Inscritos</small>
                    <a href="${pageContext.request.contextPath}/cursos?accion=detalle&id=<%= c.getId() %>"
                       style="background-color: #5c3043; color: white; text-decoration: none; padding: 8px 14px; border-radius: 6px; font-size: 0.85rem; font-weight: bold;">
                        Ver Curso
                    </a>
                </div>
            </div>
            <%
                }
            } else {
            %>
            <p style="text-align: center; grid-column: 1 / -1; color: #777;">No hay cursos disponibles en este momento.</p>
            <% } %>
        </div>
    </section>
</div>

<!-- Footer -->
<footer>
    <p>&copy; 2026 EducaParaTodos. Todos los derechos reservados.</p>
</footer>
</body>
</html>