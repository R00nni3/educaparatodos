<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <h1>EducaParaTodos</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/index.jsp">Inicio</a>
        <a href="${pageContext.request.contextPath}/cursos">Cursos</a>
        <a href="${pageContext.request.contextPath}/perfil.jsp">Mi Perfil</a>
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
    <section class="container">
        <h3>Cursos Destacados</h3>
        <!-- Bucle de cursos -->
    </section>
</div>

<!-- Footer -->
<footer>
    <p>&copy; 2026 EducaParaTodos. Todos los derechos reservados.</p>
</footer>
</body>
</html>