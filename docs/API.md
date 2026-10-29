# API v1

Base local: `http://localhost:8080/api`. JSON; fechas de vencimiento `YYYY-MM-DD`, creación en UTC. Estados: `PENDING`, `IN_PROGRESS`, `COMPLETED`.

| Método | Ruta | Resultado |
| --- | --- | --- |
| GET | /categories | Categorías por nombre |
| GET | /tasks | Página filtrada, HTTP 200 |
| POST | /tasks | Tarea creada, HTTP 201 |
| PUT | /tasks/{id} | Reemplazo de datos editables, HTTP 200 |
| DELETE | /tasks/{id} | HTTP 204 |

GET /tasks admite `search`, `status`, `categoryId`, `page` (desde 0), `size` (1–100, defecto 10), `sort` (`createdAt` o `dueDate`) y `direction` (`asc` o `desc`). Defecto: creación descendente. Los empates se ordenan por id ascendente. PostgreSQL sitúa los vencimientos nulos al final en ascendente y al principio en descendente.

Respuesta paginada: `content`, `totalElements`, `totalPages`, `number`, `size`. Los filtros se combinan con AND; la búsqueda compara título o descripción, sin distinguir mayúsculas. Los comodines % y _ se tratan como texto literal.

## Recorrido manual

Con Docker Compose iniciado:

```bash
curl http://localhost:8080/api/categories
curl -i -X POST http://localhost:8080/api/tasks -H 'Content-Type: application/json' -d '{"title":"Preparar portafolio","description":"Documentar Taskflow","status":"PENDING","categoryId":1,"dueDate":"2026-12-01"}'
curl 'http://localhost:8080/api/tasks?search=portafolio&page=0&size=5&sort=createdAt&direction=desc'
```

Usa el id devuelto por POST para actualizar y eliminar:

```bash
curl -i -X PUT http://localhost:8080/api/tasks/1 -H 'Content-Type: application/json' -d '{"title":"Preparar portafolio","description":"README listo","status":"COMPLETED","categoryId":1,"dueDate":null}'
curl -i -X DELETE http://localhost:8080/api/tasks/1
```

Comprueba HTTP 400 al enviar título vacío, categoría inexistente, estado inválido, tamaño 101 o página negativa. HTTP 404 al modificar o eliminar un id inexistente. El título admite 120 caracteres y la descripción 2000; descripción es obligatoria en el JSON y puede ser vacía. Comprueba también en la interfaz guardar, cancelar edición, borrar, cero resultados y pasar páginas con más de 10 tareas.
