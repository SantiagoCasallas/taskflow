# Taskflow

Gestor de tareas para un portafolio full stack junior. Angular 21 + Java 21 / Spring Boot 3.5 + PostgreSQL 17.

## Funcionalidades

- Crear, listar, editar y eliminar tareas con confirmación.
- Estados Pendiente, En proceso y Completada.
- Categorías predefinidas Personal, Trabajo y Estudio.
- Búsqueda por título y descripción, filtros por categoría y estado.
- Orden por creación o vencimiento, ascendente o descendente.
- Paginación y filtros ejecutados en PostgreSQL mediante Spring Data JPA.
- Validación de entradas, errores HTTP, migraciones Flyway y diseño adaptable.

## Ejecutar con Docker

Requisito: Docker con Compose.

```bash
docker compose up --build
```

Abre http://localhost:4200. La API queda en http://localhost:8080/api/tasks. PostgreSQL persiste en un volumen. `docker compose down` conserva los datos; `docker compose down -v` los elimina.

Si el puerto 8080 está ocupado, configura `TASKFLOW_API_PORT=8082` en tu archivo local `.env`. El frontend sigue en http://localhost:4200. Para las pruebas directas usa `TASKFLOW_API_URL=http://localhost:8082/api python3 scripts/smoke_api.py`.

Las credenciales locales `taskflow` son solo para desarrollo. Para cambiarlas, crea `.env` con `DB_PASSWORD=...` antes de inicializar el volumen.

## Desarrollo sin contenedores

Requisitos: Node 22.12 o superior dentro de la rama 22, pnpm 10, Java 21, Maven 3.9 y PostgreSQL 17.

```bash
docker compose up -d db
cd backend
mvn spring-boot:run
```

En otra terminal:

```bash
cd frontend
pnpm install --frozen-lockfile
pnpm start
```

Angular redirige `/api` a localhost:8080, sin necesidad de CORS. El frontend lee `public/config.json` al arrancar; por defecto usa `/api`.

## Estructura

```text
frontend/src/           Componentes Angular, formulario y listado
backend/src/main/java/ API REST, entidades y repositorios
backend/src/main/resources/db/migration/ Esquema y categorías iniciales
docs/                  Contrato API, despliegue y roadmap
.github/workflows/     Compilación y pruebas en CI
```

## Verificación

```bash
cd frontend
pnpm build
```

```bash
cd backend
mvn verify
```

Las pruebas unitarias cubren parámetros inválidos, tarea inexistente y categoría desconocida. La CI ejecuta ambos comandos. Con Docker iniciado, ejecuta `python3 scripts/smoke_api.py` y `python3 scripts/check_filters.py` para comprobar CRUD, validación, filtros, orden y paginación contra PostgreSQL. Ambos crean y eliminan sus propias tareas y aceptan la variable `TASKFLOW_API_URL`. Todavía no hay pruebas E2E del navegador.

## Documentación

- [API y recorrido manual](docs/API.md)
- [Despliegue gratuito](docs/DEPLOYMENT.md)
- [Decisiones y próximos pasos](docs/ROADMAP.md)

Esta primera versión es una demo compartida: todavía no implementa autenticación ni privacidad por usuario. Usa datos de ejemplo al publicarla.

## Estado de esta entrega

Verificados: compilación de Angular, `mvn verify` con 3 pruebas unitarias, arranque con Docker, migración Flyway en PostgreSQL 17 y recorridos de integración tanto por la API como por Nginx. El proyecto aún no está publicado en GitHub ni en un proveedor.
# taskflow
