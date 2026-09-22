<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>EducaParaTodos - Cursos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>

<!-- Encabezado -->
<header class="main-header">
    <h1>EducaParaTodos</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/index.jsp">Inicio</a>
        <a href="${pageContext.request.contextPath}/cursos">Cursos</a>
        <a href="${pageContext.request.contextPath}/perfil.jsp">Mi Perfil</a>
    </nav>
</header>

<!-- Contenido Principal que empuja el footer -->
<div class="main-content">
    <div class="container">
        <h3>Catálogo de Cursos</h3>

        <!-- Buscador-->
        <form action="${pageContext.request.contextPath}/cursos" method="get" class="search-form">
            <input type="text" name="tema" placeholder="Buscar por tema...">
            <button type="submit" class="btn-details">Filtrar</button>
        </form>

        <!-- Grilla donde se mostrarán los cursos dinámicamente -->
        <div class="courses-grid" style="margin-top: 2rem;">
            <c:forEach var="curso" items="${cursos}">
                <div class="course-card">
                    <h4>${curso.titulo}</h4>
                    <p>${curso.descripcion}</p>
                    <span class="badge">${curso.nivel}</span>
                    <a href="${pageContext.request.contextPath}/cursos?accion=detalle&id=${curso.id}" class="btn-details">Ver Curso</a>
                </div>
            </c:forEach>
        </div>
    </div>
</div>

<!-- Footer anclado abajo -->
<footer>
    <p>&copy; 2026 EducaParaTodos. Todos los derechos reservados.</p>
</footer>

</body>
</html>