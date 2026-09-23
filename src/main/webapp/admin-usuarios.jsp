<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.educaparatodos.model.Usuario" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Usuarios - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>

<!-- Encabezado -->
<header class="main-header">
    <h1><a href="${pageContext.request.contextPath}/index.jsp" style="color: white; text-decoration: none;">EducaParaTodos</a></h1>
    <nav>
        <a href="${pageContext.request.contextPath}/index.jsp">Inicio</a>
        <a href="${pageContext.request.contextPath}/cursos">Cursos</a>
        <a href="${pageContext.request.contextPath}/mi-perfil">Mi Perfil</a>
        <a href="${pageContext.request.contextPath}/logout">Cerrar Sesión</a>
    </nav>
</header>

<!-- Contenido Principal -->
<div class="main-content container" style="margin-top: 2rem;">

    <h2 style="text-align: center; color: #4a2d40; margin-bottom: 2rem;">Gestión de Usuarios y Roles</h2>

    <%-- Mensaje informativo si existe en sesión --%>
    <% if (session.getAttribute("mensaje") != null) { %>
    <div class="alert-message alert-success" style="margin-bottom: 1.5rem; text-align: center;">
        <%= session.getAttribute("mensaje") %>
    </div>
    <% session.removeAttribute("mensaje"); %>
    <% } %>

    <div class="profile-card" style="max-width: 900px; margin: 0 auto; background: white; padding: 2rem; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">

        <table style="width: 100%; border-collapse: collapse; text-align: left;">
            <thead>
            <tr style="border-bottom: 2px solid #eee; color: #4a2d40;">
                <th style="padding: 12px;">ID</th>
                <th style="padding: 12px;">Nombre</th>
                <th style="padding: 12px;">Email</th>
                <th style="padding: 12px;">Rol Actual</th>
                <th style="padding: 12px;">Acción</th>
            </tr>
            </thead>
            <tbody>
            <%
                List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
                if (usuarios != null && !usuarios.isEmpty()) {
                    for (Usuario u : usuarios) {
            %>
            <form id="form-editar-<%= u.getId() %>" action="${pageContext.request.contextPath}/usuarios" method="post">
                <input type="hidden" name="accion" value="actualizar">
                <input type="hidden" name="id" value="<%= u.getId() %>">
            </form>

            <tr style="border-bottom: 1px solid #f0f0f0;">
                <td style="padding: 12px;"><%= u.getId() %></td>

                <td style="padding: 12px;">
                    <input form="form-editar-<%= u.getId() %>" type="text" name="nombre" value="<%= u.getNombre() %>"
                           style="padding: 6px; border-radius: 6px; border: 1px solid #ccc; width: 130px;">
                </td>

                <td style="padding: 12px;">
                    <input form="form-editar-<%= u.getId() %>" type="email" name="email" value="<%= u.getEmail() %>"
                           style="padding: 6px; border-radius: 6px; border: 1px solid #ccc; width: 170px;">
                </td>

                <!-- Columna: Rol Actual -->
                <td style="padding: 12px;">
                    <strong><%= u.getRol() %></strong>
                </td>

                <!-- Columna: Acción (Combobox + Botones) -->
                <td style="padding: 12px;">
                    <div style="display: flex; flex-direction: column; gap: 8px;">
                        <select form="form-editar-<%= u.getId() %>" name="rol" class="form-control"
                                style="padding: 6px 10px; border-radius: 6px; border: 1px solid #ccc; width: 100%;">
                            <option value="ESTUDIANTE" <%= "ESTUDIANTE".equalsIgnoreCase(u.getRol().toString()) ? "selected" : "" %>>ESTUDIANTE</option>
                            <option value="PROFESOR" <%= ("PROFESOR".equalsIgnoreCase(u.getRol().toString()) || "INSTRUCTOR".equalsIgnoreCase(u.getRol().toString())) ? "selected" : "" %>>PROFESOR</option>
                            <option value="ADMIN" <%= "ADMIN".equalsIgnoreCase(u.getRol().toString()) ? "selected" : "" %>>ADMIN</option>
                        </select>

                        <div style="display: flex; gap: 8px;">
                            <button form="form-editar-<%= u.getId() %>" type="submit"
                                    style="background-color: #5c3043; color: white; border: none; padding: 7px 14px; border-radius: 6px; cursor: pointer; font-weight: bold; flex: 1;">
                                Guardar
                            </button>

                            <form action="${pageContext.request.contextPath}/usuarios" method="post" style="margin: 0; flex: 1;"
                                  onsubmit="return confirm('¿Eliminar permanentemente a <%= u.getNombre() %>? Esto también borrará sus inscripciones.');">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="<%= u.getId() %>">
                                <button type="submit" style="background-color: #a83232; color: white; border: none; padding: 7px 14px; border-radius: 6px; cursor: pointer; font-weight: bold; width: 100%;">
                                    Eliminar
                                </button>
                            </form>
                        </div>
                    </div>
                </td>
            </tr>
            <%
                }
            } else {
            %>
            <tr>
                <td colspan="5" style="padding: 12px; text-align: center; color: #888;">No hay usuarios registrados.</td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>

    <!-- TARJETA DE CONFIRMACIÓN (Oculta por defecto) -->
    <div id="seccion-confirmacion" style="display: none; max-width: 900px; margin: 2rem auto 0 auto; background: #fff8f0; border: 2px solid #d97706; padding: 1.5rem; border-radius: 12px; text-align: center; box-shadow: 0 4px 10px rgba(0,0,0,0.08);">
        <h4 style="margin: 0 0 10px 0; color: #92400e; font-size: 1.2rem;">Confirmar Cambio de Rol</h4>
        <p id="texto-confirmacion" style="color: #451a03; font-size: 1.05rem; margin-bottom: 1.5rem;"></p>

        <form action="${pageContext.request.contextPath}/usuarios" method="post" style="display: inline-block;">
            <input type="hidden" name="accion" value="cambiarRol">
            <input type="hidden" id="input-usuario-id" name="usuarioId" value="">
            <input type="hidden" id="input-nuevo-rol" name="nuevoRol" value="">

            <button type="submit" style="background-color: #2e7d32; color: white; border: none; padding: 10px 30px; font-size: 1.1rem; border-radius: 6px; cursor: pointer; font-weight: bold; margin-right: 10px;">
                OK
            </button>

            <button type="button" onclick="cancelarConfirmacion()" style="background-color: #757575; color: white; border: none; padding: 10px 20px; font-size: 1rem; border-radius: 6px; cursor: pointer;">
                Cancelar
            </button>
        </form>
    </div>

    <!-- SECCIÓN DE OPERACIONES MASIVAS -->
    <div style="max-width: 900px; margin: 2rem auto; display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">

        <!-- UPDATE masivo por Dominio -->
        <div style="background: white; padding: 1.5rem; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">
            <h4 style="margin-top: 0; color: #d97706;">🎓 Ascender por dominios de correo</h4>
            <p style="font-size: 0.9rem; color: #666;">Agrega uno o más dominios, luego asciende de una sola vez a todos los estudiantes que coincidan.</p>

            <div style="margin-bottom: 1rem;">
                <label style="display: block; font-size: 0.85rem; font-weight: bold; margin-bottom: 4px;">Dominio (sin @):</label>
                <div style="display: flex; gap: 8px;">
                    <input type="text" id="nuevoDominio" placeholder="instituto.cl" class="form-control" style="flex: 1; padding: 8px; border-radius: 6px; border: 1px solid #ccc;">
                    <button type="button" onclick="agregarDominio()" style="background-color: #5c3043; color: white; border: none; padding: 8px 16px; border-radius: 6px; cursor: pointer;">Agregar</button>
                </div>
            </div>

            <ul id="listaDominios" style="list-style: none; padding: 0; margin: 0 0 1rem 0;"></ul>

            <form action="${pageContext.request.contextPath}/usuarios" method="post" id="formAscenderDominios"
                  onsubmit="return confirmarEnvioDominios();">
                <input type="hidden" name="accion" value="ascenderPorDominio">
                <input type="hidden" name="dominios" id="inputDominios" value="">
                <button type="submit" style="background-color: #d97706; color: white; border: none; padding: 8px 16px; border-radius: 6px; cursor: pointer; width: 100%; font-weight: bold;">
                    Ascender (masivo)
                </button>
            </form>
        </div>

        <!-- DELETE masivo -->
        <div style="background: white; padding: 1.5rem; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">
            <h4 style="margin-top: 0; color: #991b1b;">🗑️ Eliminar estudiantes sin inscripción</h4>
            <p style="font-size: 0.9rem; color: #666;">Elimina a los estudiantes que nunca se han inscrito en ningún curso Y que se registraron antes de la fecha indicada.</p>

            <form action="${pageContext.request.contextPath}/usuarios" method="post" style="margin-top: 1rem;">
                <input type="hidden" name="accion" value="eliminarSinInscripcion">
                <div style="margin-bottom: 1rem;">
                    <label style="display: block; font-size: 0.85rem; font-weight: bold; margin-bottom: 4px;">Registrados antes de:</label>
                    <input type="date" name="fechaLimite" required class="form-control" style="width: 100%; padding: 8px; border-radius: 6px; border: 1px solid #ccc;">
                </div>
                <button type="submit" onclick="return confirm('⚠️ Se eliminarán PERMANENTEMENTE los estudiantes sin inscripciones registrados antes de esa fecha. ¿Continuar?');" style="background-color: #a83232; color: white; border: none; padding: 8px 16px; border-radius: 6px; cursor: pointer; width: 100%; font-weight: bold;">
                    Eliminar (masivo)
                </button>
            </form>
        </div>

    </div>

</div>

<!-- Footer -->
<footer style="margin-top: 4rem;">
    <p>&copy; 2026 EducaParaTodos. Todos los derechos reservados.</p>
</footer>

<script>
    function prepararConfirmacion(id, nombre, selectId) {
        var selectElem = document.getElementById(selectId);
        var nuevoRol = selectElem.value;

        document.getElementById('input-usuario-id').value = id;
        document.getElementById('input-nuevo-rol').value = nuevoRol;

        document.getElementById('texto-confirmacion').innerHTML =
            '¿Deseas cambiar el rol de <strong>' + nombre + '</strong> a <strong>' + nuevoRol + '</strong>?';

        var panel = document.getElementById('seccion-confirmacion');
        panel.style.display = 'block';
        panel.scrollIntoView({ behavior: 'smooth' });
    }

    function cancelarConfirmacion() {
        document.getElementById('seccion-confirmacion').style.display = 'none';
    }

    let dominiosAgregados = [];

    function agregarDominio() {
        const input = document.getElementById('nuevoDominio');
        const valor = input.value.trim().toLowerCase();

        if (valor === '') return;
        if (dominiosAgregados.includes(valor)) {
            alert('Ese dominio ya está en la lista.');
            return;
        }

        dominiosAgregados.push(valor);
        input.value = '';
        renderizarListaDominios();
    }

    function quitarDominio(dominio) {
        dominiosAgregados = dominiosAgregados.filter(d => d !== dominio);
        renderizarListaDominios();
    }

    function renderizarListaDominios() {
        const lista = document.getElementById('listaDominios');
        lista.innerHTML = '';

        dominiosAgregados.forEach(dominio => {
            const li = document.createElement('li');
            li.style = 'display:flex; justify-content:space-between; align-items:center; background:#fff3e0; padding:6px 10px; border-radius:6px; margin-bottom:6px;';
            li.innerHTML = '<span>@' + dominio + '</span>' +
                '<button type="button" onclick="quitarDominio(\'' + dominio + '\')" style="background:none; border:none; color:#991b1b; cursor:pointer; font-weight:bold;">✕</button>';
            lista.appendChild(li);
        });

        document.getElementById('inputDominios').value = dominiosAgregados.join(',');
    }

    function confirmarEnvioDominios() {
        if (dominiosAgregados.length === 0) {
            alert('Agrega al menos un dominio antes de continuar.');
            return false;
        }
        return confirm('¿Ascender a INSTRUCTOR a los estudiantes de ' + dominiosAgregados.length + ' dominio(s)?');
    }
</script>

</body>
</html>