<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Iniciar Sesión - EducaParaTodos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<header class="main-header">
    <h1><a href="${pageContext.request.contextPath}/index.jsp" style="color: white; text-decoration: none;">EducaParaTodos</a></h1>
    <nav>
        <a href="${pageContext.request.contextPath}/index.jsp">Inicio</a>
        <a href="${pageContext.request.contextPath}/cursos">Cursos</a>
        <a href="${pageContext.request.contextPath}/registro.jsp">Registrarse</a>
    </nav>
</header>

<div class="main-content">
    <div class="form-container-left">
        <h2>Iniciar Sesión</h2>

        <% if ("1".equals(request.getParameter("error"))) { %>
        <div class="alert-message alert-error">
            Correo o contraseña incorrectos.
        </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="form-group">
                <label for="email">Correo Electrónico</label>
                <input type="email" id="email" name="email" class="form-control" placeholder="ejemplo@correo.com" required>
            </div>

            <div class="form-group">
                <label for="password">Contraseña</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="••••••••" required>
            </div>

            <button type="submit" class="btn-details">Ingresar</button>
        </form>

        <div class="auth-footer-link">
            ¿No tienes una cuenta? <a href="${pageContext.request.contextPath}/registro.jsp">Regístrate aquí</a>
        </div>
    </div>
</div>

<footer>
    <p>&copy; 2026 EducaParaTodos. Todos los derechos reservados https://github.com/R00nni3.</p>
</footer>
</body>
</html>