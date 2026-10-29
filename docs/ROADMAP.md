# Decisiones y roadmap

## Primera entrega

Monorepo con frontend y backend separados. Angular standalone con formularios y señales; Spring MVC, JPA Specifications para filtros combinables, DTO de entrada validado y respuesta de paginación explícita. PostgreSQL guarda las tareas; Flyway versiona su esquema y evita depender de `ddl-auto=update`.

Categorías predefinidas en esta entrega. No existe todavía CRUD de categorías. La fecha de vencimiento es opcional; la creación se fija en el servidor. La búsqueda usa LIKE escapado, adecuado para una demo pequeña; un volumen grande necesitaría índices de texto y medir consultas.

## Siguiente entrega: autenticación y privacidad

1. Añadir usuarios y migración de propiedad de tareas. Decidir qué hacer con las tareas públicas existentes antes de migrar datos.
2. Incorporar Spring Security, contraseñas con hash y sesiones o tokens según el despliegue elegido.
3. Derivar siempre el propietario de la identidad autenticada en el backend.
4. Aplicar el propietario a consultas, edición y borrado; nunca confiar en un `userId` enviado por el navegador.
5. Añadir pruebas que comprueben que un usuario no puede leer ni modificar tareas ajenas.
6. Añadir registro, inicio/cierre de sesión y manejo de sesión expirada en Angular.

## Mejoras posteriores

CRUD de categorías, pruebas con PostgreSQL mediante Testcontainers, pruebas E2E, foco contenido en el diálogo y restauración al cerrar, capturas del producto para GitHub y vídeo breve del flujo completo. El repositorio aún no incluye una licencia: elegirla antes de presentar el código como reutilizable.
