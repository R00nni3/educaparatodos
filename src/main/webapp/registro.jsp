<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registro - Educa Para Todos</title>
</head>
<body>
<h2>Crea tu cuenta</h2>
<form action="${pageContext.request.contextPath}/usuarios" method="post">
    <!-- Parámetro oculto para que el servlet sepa qué hacer -->
    <input type="hidden" name="accion" value="crear">

    <label for="nombre">Nombre completo:</label><br>
    <input type="text" id="nombre" name="nombre" required><br><br>

    <label for="email">Correo electrónico:</label><br>
    <input type="email" id="email" name="email" required><br><br>

    <label for="password">Contraseña:</label><br>
    <input type="password" id="password" name="password" required><br><br>

    <button type="submit">Registrarme</button>
</form>
<br>
<a href="${pageContext.request.contextPath}/login.jsp">¿Ya tienes cuenta? Inicia sesión</a>
</body>
</html>