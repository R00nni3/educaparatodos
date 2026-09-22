<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
    <h1>EducaParaTodos</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/index.jsp">Inicio</a>
        <a href="${pageContext.request.contextPath}/cursos">Cursos</a>
        <a href="${pageContext.request.contextPath}/perfil.jsp">Mi Perfil</a>
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

        <!-- Botón de acción (Inscribirse) -->
        <form action="${pageContext.request.contextPath}/cursos" method="post">
            <input type="hidden" name="accion" value="inscribir">
            <input type="hidden" name="cursoId" value="${curso.id}">
            <button type="submit" class="btn-details" style="padding: 0.75rem 2rem; font-size: 1rem; cursor: pointer; border: none;">
                Inscribirme en este Curso
            </button>
        </form>

    </div>
</div>

<!-- Footer unificado -->
<footer>
    <p>&copy; 2026 EducaParaTodos. Todos los derechos reservados.</p>
</footer>

</body>
</html>
