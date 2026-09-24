<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.educaparatodos.model.Curso" %>
<%@ page import="com.educaparatodos.model.Usuario" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>EducaParaTodos - Formulario Curso</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<header class="main-header">
    <h1><a href="${pageContext.request.contextPath}/index.jsp" style="color: white; text-decoration: none;">EducaParaTodos</a></h1>
    <nav>
        <a href="${pageContext.request.contextPath}/index.jsp">Inicio</a>
        <a href="${pageContext.request.contextPath}/cursos">Cursos</a>
        <a href="${pageContext.request.contextPath}/mi-perfil">Mi Perfil</a>
    </nav>
</header>

<div class="main-content">
    <div class="form-container-left">
        <%
            Curso curso = (Curso) request.getAttribute("curso");
            boolean esEdicion = (curso != null);
        %>
        <h2><%= esEdicion ? "Editar Curso" : "Crear Nuevo Curso" %></h2>

        <form action="${pageContext.request.contextPath}/cursos" method="post">
            <input type="hidden" name="accion" value="<%= esEdicion ? "actualizar" : "crear" %>">
            <% if (esEdicion) { %>
            <input type="hidden" name="id" value="<%= curso.getId() %>">
            <% } %>

            <div class="form-group">
                <label for="titulo">Título del Curso</label>
                <input type="text" id="titulo" name="titulo" class="form-control" value="<%= esEdicion ? curso.getTitulo() : "" %>" required>
            </div>

            <div class="form-group">
                <label for="tema">Tema Principal</label>
                <input type="text" id="tema" name="tema" class="form-control" value="<%= esEdicion ? curso.getTema() : "" %>" required>
            </div>

            <div class="form-group">
                <label for="nivel">Nivel de Dificultad</label>
                <select id="nivel" name="nivel" class="form-control" required>
                    <option value="PRINCIPIANTE" <%= esEdicion && "PRINCIPIANTE".equals(curso.getNivel().toString()) ? "selected" : "" %>>Principiante</option>
                    <option value="INTERMEDIO" <%= esEdicion && "INTERMEDIO".equals(curso.getNivel().toString()) ? "selected" : "" %>>Intermedio</option>
                    <option value="AVANZADO" <%= esEdicion && "AVANZADO".equals(curso.getNivel().toString()) ? "selected" : "" %>>Avanzado</option>
                </select>
            </div>

            <div class="form-group">
                <label for="descripcion">Descripción</label>
                <textarea id="descripcion" name="descripcion" class="form-control" rows="4" required><%= esEdicion && curso.getDescripcion() != null ? curso.getDescripcion() : "" %></textarea>
            </div>

            <button type="submit" class="btn-details">
                <%= esEdicion ? "Guardar Cambios" : "Crear Curso" %>
            </button>
            <a href="${pageContext.request.contextPath}/cursos" style="margin-left: 10px; color: #666; text-decoration: none;">Cancelar</a>
        </form>
    </div>
</div>

<footer>
    <p>&copy; 2026 EducaParaTodos. Todos los derechos reservados https://github.com/R00nni3.</p>
</footer>
</body>
</html>