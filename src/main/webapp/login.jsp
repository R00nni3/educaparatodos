<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Iniciar Sesión - Educa Para Todos</title>
</head>
<body>
<h2>Iniciar Sesión</h2>

<%-- Si hay un mensaje de error (ej. credenciales inválidas), lo mostramos aquí --%>
<%
    String error = request.getParameter("error");
    if (error != null) {
%>
<p style="color: red;">Error al iniciar sesión. Por favor, intenta de nuevo.</p>
<% } %>

<%-- El formulario apuntará a un futuro LoginServlet --%>
<form action="${pageContext.request.contextPath}/login" method="post">
    <label for="email">Correo electrónico:</label><br>
    <input type="email" id="email" name="email" required><br><br>

    <label for="password">Contraseña:</label><br>
    <input type="password" id="password" name="password" required><br><br>

    <button type="submit">Ingresar</button>
</form>

<br>
<a href="${pageContext.request.contextPath}/registro.jsp">¿No tienes cuenta? Regístrate aquí</a>
<a href="${pageContext.request.contextPath}/cursos">Volver a los cursos</a>
</body>
</html>