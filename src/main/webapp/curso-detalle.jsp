<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.educaparatodos.model.Usuario" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>EducaParaTodos - ${curso.titulo}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>

<!-- Encabezado unificado -->
<header class="main-header">
    <h1><a href="${pageContext.request.contextPath}/index.jsp" style="color: white; text-decoration: none;">EducaParaTodos</a></h1>
    <nav>
        <a href="${pageContext.request.contextPath}/index.jsp">Inicio</a>
        <a href="${pageContext.request.contextPath}/cursos">Cursos</a>

        <%
            if (session.getAttribute("usuarioLogueado") != null) {
        %>
        <a href="${pageContext.request.contextPath}/mi-perfil">Mi Perfil</a>
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

<!-- Contenido Principal -->
<div class="main-content">
    <div class="container" style="max-width: 800px; background: white; padding: 2.5rem; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-top: 2rem;">

        <!-- Enlace para volver atrás -->
        <a href="${pageContext.request.contextPath}/cursos" style="color: #6b3e5a; text-decoration: none; font-weight: bold; display: inline-block; margin-bottom: 1.5rem;">
            &larr; Volver al Catálogo
        </a>

        <!-- Información del Curso -->
        <span class="badge" style="margin-bottom: 1rem;">${curso.nivel}</span>
        <h2 style="color: #4a2d40; font-size: 2rem; margin-bottom: 1rem;">${curso.titulo}</h2>

        <p style="color: #555; font-size: 1.1rem; line-height: 1.6; margin-bottom: 2rem;">
            ${curso.descripcion}
        </p>

        <div style="background: #f9f6f8; padding: 1.5rem; border-radius: 8px; margin-bottom: 2rem; border-left: 4px solid #6b3e5a;">
            <p style="margin: 0; color: #4a2d40; font-weight: 500;">
                <strong>Tema principal:</strong> ${curso.tema}
            </p>
        </div>

        <%-- Alertas --%>
        <% if ("yaInscrito".equals(request.getParameter("error"))) { %>
        <div class="alert-message alert-error" style="margin-bottom: 1.5rem;">
            ¡Ya te encuentras inscrito en este curso! Puedes revisarlo en tu perfil.
        </div>
        <% } else if ("noAutorizado".equals(request.getParameter("error"))) { %>
        <div class="alert-message alert-error" style="margin-bottom: 1.5rem;">
            No tienes permisos suficientes para realizar esta acción.
        </div>
        <% } %>

        <!-- Botón de Inscripción -->
        <form action="${pageContext.request.contextPath}/cursos" method="post" onsubmit="return confirm('¿Estás seguro/a de que deseas inscribirte en este curso?');">
            <input type="hidden" name="accion" value="inscribir">
            <input type="hidden" name="cursoId" value="${curso.id}">
            <button type="submit" class="btn-details" style="padding: 0.75rem 2rem; font-size: 1rem; cursor: pointer; border: none;">
                Inscribirme en este Curso
            </button>
        </form>

        <%-- Botones de Edición / Eliminación según el Rol --%>
        <%
            Usuario uLog = (Usuario) session.getAttribute("usuarioLogueado");
            if (uLog != null) {
                String rol = uLog.getRol() != null ? uLog.getRol().toString().toUpperCase() : "";
                boolean esAdmin = "ADMIN".equals(rol);
                boolean esProfe = "PROFESOR".equals(rol) || "INSTRUCTOR".equals(rol);

                if (esAdmin || esProfe) {
        %>
        <div style="margin-top: 2rem; padding-top: 1.5rem; border-top: 1px solid #eee; display: flex; gap: 10px;">
            <%-- ADMIN Y PROFESOR PUEDEN EDITAR --%>
            <a href="${pageContext.request.contextPath}/cursos?accion=editar&id=${curso.id}" class="btn-details" style="background-color: #d97706; text-decoration: none; display: inline-block;">
                ✏️ Editar Curso
            </a>

            <%-- SOLO ADMIN PUEDE ELIMINAR --%>
            <% if (esAdmin) { %>
            <form action="${pageContext.request.contextPath}/cursos" method="post" onsubmit="return confirm('¿Estás seguro/a de eliminar permanentemente este curso?');" style="margin: 0;">
                <input type="hidden" name="accion" value="eliminar">
                <input type="hidden" name="id" value="${curso.id}">
                <button type="submit" class="btn-details" style="background-color: #a83232; border: none; cursor: pointer;">
                    🗑️ Eliminar Curso
                </button>
            </form>
            <% } %>
        </div>
        <%
                }
            }
        %>

    </div>
</div>

<footer>
    <p>&copy; 2026 EducaParaTodos. Todos los derechos reservados https://github.com/R00nni3.</p>
</footer>

</body>
</html>