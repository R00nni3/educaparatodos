<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Cursos Disponibles - EducaParaTodos</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
<header>
    <h1>EducaParaTodos</h1>
    <nav>
        <a href="index.jsp">Inicio</a>
        <a href="cursos">Cursos</a>
        <a href="perfil.jsp">Mi Perfil</a>
    </nav>
</header>

<div class="container">
    <h2>Catálogo de Cursos</h2>

    <!-- Formulario de búsqueda por tema o dificultad (usando JPQL) -->
    <form action="cursos" method="get" style="margin-bottom: 20px;">
        <input type="text" name="busqueda" placeholder="Buscar por tema..." style="padding: 8px; width: 250px;">
        <button type="submit" style="padding: 8px 15px;">Filtrar</button>
    </form>

    <div class="card-grid">
        <!-- Bucle JSTL para recorrer la lista de cursos enviada por el Servlet -->
        <c:forEach var="curso" items="${listaCursos}">
            <div class="card">
                <h3>${curso.titulo}</h3>
                <p>${curso.descripcion}</p>
                <p><strong>Tema:</strong> ${curso.tema}</p>
                <p><strong>Nivel:</strong> ${curso.nivel}</p>
                <a href="curso-detalle?id=${curso.id}">Ver detalles</a>
            </div>
        </c:forEach>
    </div>
</div>
</body>
</html>