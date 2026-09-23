<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registro - EducaParaTodos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<header class="main-header">
    <h1><a href="${pageContext.request.contextPath}/index.jsp" style="color: white; text-decoration: none;">EducaParaTodos</a></h1>
    <nav>
        <a href="${pageContext.request.contextPath}/index.jsp">Inicio</a>
        <a href="${pageContext.request.contextPath}/cursos">Cursos</a>
        <a href="${pageContext.request.contextPath}/login.jsp">Iniciar Sesión</a>
    </nav>
</header>

<div class="main-content">
    <div class="form-container-left">
        <h2>Crear Cuenta</h2>

        <form action="${pageContext.request.contextPath}/usuarios" method="post">
            <input type="hidden" name="accion" value="crear">

            <div class="form-group-aligned">
                <label for="nombre">Nombre Completo</label>
                <input type="text" id="nombre" name="nombre" placeholder="Juan Pérez" required>
            </div>

            <div class="form-group-aligned">
                <label for="email">Correo Electrónico</label>
                <input type="email" id="email" name="email" placeholder="ejemplo@correo.com" required>
            </div>

            <div class="form-group-aligned">
                <label for="password">Contraseña</label>
                <input type="password" id="password" name="password" placeholder="••••••••" required>
            </div>

            <button type="submit" class="btn-form">Registrarse</button>
        </form>

        <p style="margin-top: 1.5rem;">
            ¿Ya tienes cuenta? <a href="${pageContext.request.contextPath}/login.jsp" style="color: #845ec2; font-weight: bold;">Inicia sesión</a>
        </p>
    </div>
</div>

<footer>
    <p>&copy; 2026 EducaParaTodos. Todos los derechos reservados.</p>
</footer>
</body>
</html>