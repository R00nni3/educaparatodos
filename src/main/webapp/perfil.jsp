<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mi Perfil - Educa Para Todos</title>
</head>
<body>
<h2>Mi Perfil</h2>

<%-- Verificamos si hay un usuario logueado en la sesión --%>
<%
    com.educaparatodos.model.Usuario usuario = (com.educaparatodos.model.Usuario) session.getAttribute("usuarioLogueado");
    if (usuario != null) {
%>
<p><strong>Nombre:</strong> <%= usuario.getNombre() %></p>
<p><strong>Email:</strong> <%= usuario.getEmail() %></p>
<p><strong>Rol:</strong> <%= usuario.getRol() %></p>

<hr>
<h3>Mis Cursos Inscritos</h3>
<p><em>(Aquí conectaremos pronto tus cursos guardados)</em></p>

<br>
<a href="${pageContext.request.contextPath}/cursos">Explorar más cursos</a>
<% } else { %>
<p>No has iniciado sesión o tu sesión ha expirado.</p>
<a href="${pageContext.request.contextPath}/login.jsp">Ir a iniciar sesión</a>
<% } %>
</body>
</html>