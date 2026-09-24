# EducaParaTodos

Plataforma educativa en línea desarrollada para la organización sin fines de lucro ficticia EducaParaTodos, que ofrece cursos gratuitos. Proyecto académico para la asignatura de Desarrollo Web II (IPCHILE), enfocado en el mapeo objeto-relacional con JPA/Hibernate y una arquitectura por capas en J2EE.

## Resultados de Aprendizaje cubiertos

- **RA3**: Uso de Java Persistence API (JPA) como alternativa de mapeo objeto-relacional.
- **RA4**: Sistema web programado sobre una arquitectura por capas en J2EE (Modelo → DAO → Controlador → Vista).

## Tecnologías utilizadas

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 11 |
| Build tool | Maven |
| Mapeo objeto-relacional | JPA 2.2 + Hibernate 5.6.15 |
| Base de datos | MySQL 8 |
| Servidor de aplicaciones | Apache Tomcat 9.0.x |
| Controladores | Servlets (`javax.servlet`, Servlet 4.0) |
| Vistas | JSP + JSTL |
| Control de versiones | Git / GitHub |

> **Nota sobre la versión de Tomcat:** el proyecto usa el namespace `javax.*` (Java EE 8), por lo que requiere **Tomcat 9.x**. Tomcat 10+ usa el namespace `jakarta.*` y no reconocerá las anotaciones `@WebServlet`/`@WebListener` de este proyecto.

## Estructura del proyecto

```
educaparatodos/
├── pom.xml
└── src/main/
    ├── java/com/educaparatodos/
    │   ├── model/         # Entidades JPA: Usuario, Curso, Leccion, Inscripcion (+ enums)
    │   ├── dao/            # Acceso a datos: CursoDAO, UsuarioDAO (CRUD, JPQL, operaciones masivas)
    │   ├── controller/     # Servlets y listener de ciclo de vida
    │   └── util/           # JPAUtil (EntityManagerFactory), PasswordUtil (hash de contraseñas)
    ├── resources/META-INF/
    │   └── persistence.xml # Unidad de persistencia JPA / conexión a MySQL
    └── webapp/
        ├── css/styles.css
        ├── WEB-INF/
        ├── index.jsp
        ├── login.jsp
        ├── registro.jsp
        ├── cursos.jsp
        ├── curso-detalle.jsp
        ├── curso-form.jsp
        ├── perfil.jsp
        └── admin-usuarios.jsp
```

## Modelo de datos

- **Usuario**: id, nombre, email (único), password (hasheada), rol, fechaRegistro.
- **Curso**: id, título, descripción, tema, nivel de dificultad, popularidad, fechaCreación.
- **Leccion**: id, título, contenido, relación `@ManyToOne` con Curso.
- **Inscripcion**: entidad de asociación entre Usuario y Curso (relación muchos-a-muchos con datos propios), con fechaInscripción.

### Relaciones

- `Usuario` 1 — N `Inscripcion` N — 1 `Curso` (muchos-a-muchos vía entidad intermedia)
- `Curso` 1 — N `Leccion`

## Roles y permisos

| Rol | Permisos |
|---|---|
| **ESTUDIANTE** | Ver cursos, inscribirse/cancelar inscripción, ver su perfil |
| **INSTRUCTOR** (mostrado como "PROFESOR" en la UI) | Editar cursos existentes |
| **ADMIN** | CRUD completo de cursos y usuarios, operaciones masivas |

## Funcionalidades principales

- Registro y login de usuarios (contraseña hasheada, sesión con `HttpSession`).
- Catálogo de cursos con búsqueda por tema (consultas JPQL).
- Inscripción y cancelación de inscripción a cursos, con conteo automático de popularidad.
- Perfil de usuario con listado de cursos inscritos.
- Panel de administración: gestión de cursos (crear/editar/eliminar) y usuarios (editar/eliminar/cambiar rol).

## Operaciones masivas (UPDATE / DELETE)

Implementadas con `executeUpdate()` de JPQL para modificar/eliminar directamente en la base de datos, sin cargar entidades en memoria una por una:

**Sobre Usuario:**
- `ascenderPorDominios`: asciende a INSTRUCTOR a estudiantes cuyo email pertenezca a uno o más dominios especificados (consulta JPQL construida dinámicamente).
- `eliminarEstudiantesSinInscripcionAntesDe`: elimina estudiantes sin ninguna inscripción, registrados antes de una fecha dada (usa una subconsulta sobre `Inscripcion`).

## Configuración y ejecución

### 1. Base de datos

Requiere MySQL 8 corriendo localmente. La base de datos se crea automáticamente al desplegar (`createDatabaseIfNotExist=true`), y las tablas se generan a partir de las entidades (`hibernate.hbm2ddl.auto=update`).

### 2. Contraseña de MySQL (variable de entorno)

Por seguridad, la contraseña real de MySQL **no** se guarda en `persistence.xml` (que sí se sube al repositorio). En su lugar, se lee de una variable de entorno:

```
MYSQL_PASSWORD=tu_clave_real
```

`JPAUtil` sobreescribe la propiedad de conexión con este valor al arrancar la aplicación. Configúrala en tu sistema operativo y reinicia el IDE antes de correr el proyecto.

### 3. Servidor

1. Descarga Apache **Tomcat 9.0.x** y configúralo como Application Server en tu IDE.
2. Despliega el artefacto `educaparatodos:war exploded` (o `.war`) en ese Tomcat.
3. Accede a `http://localhost:8080/educaparatodos_war_exploded/`.

## Entrega

Este repositorio incluye el código fuente completo (entidades, DAOs, controladores, vistas) y este documento con el detalle de diseño y funcionamiento del sistema. Se han aplicado métodos de DELETE que elimina de la Base de Datos por cumplimiento de la rúbrica.
Idealmente no se elimina **NADA** de la Base de Datos.
El proyecto tiene aún aspectos a mejorar, como por ejemplo, autenticación de cuentas, por lo que está en crecimiento. 

## Desafíos encontrados

Tuve problemas con el namespace que me mantuvo con error durante varias horas, por lo que debí cambiar de Tomcat 10.x a Tomcat 9.x

## Desarrollado por

Rosario González Perucich
Proyecto realizado para Desarrollo Web II — IPChile. Docente Sabina Romero
