# Despliegue gratuito

Propuesta: un servicio Docker en Render que sirve Angular y Spring Boot juntos, y PostgreSQL en Neon. Un único origen simplifica el despliegue y evita configurar CORS.

Consulta los límites vigentes antes de activar servicios: [Render Free](https://render.com/docs/free), [Neon](https://neon.com/pricing). Render puede suspender servicios gratuitos por inactividad; el primer acceso puede tardar. Su PostgreSQL gratuito expira a los 30 días, por eso esta guía usa Neon. El nivel gratuito está sujeto a cuotas y cambios del proveedor.

1. Sube el repositorio a tu GitHub.
2. Crea un proyecto gratuito de Neon. Copia host, base, usuario y contraseña.
3. En Render crea un **Web Service**, conecta el repositorio y selecciona runtime Docker, Dockerfile `Dockerfile` en la raíz y plan Free.
4. Configura variables privadas en Render:
   - `DB_URL=jdbc:postgresql://HOST/BASE?sslmode=require`
   - `DB_USERNAME=USUARIO`
   - `DB_PASSWORD=CONTRASEÑA`
5. Despliega. Render proporciona `PORT`; Spring lo lee automáticamente. Flyway crea el esquema. Comprueba `/api/categories` y el flujo completo en la interfaz.

No copies la cadena `postgresql://usuario:contraseña@...` directamente en DB_URL: JDBC necesita el prefijo `jdbc:postgresql://` y las credenciales separadas. No subas secretos al repositorio. Si el servicio no inicia, revisa logs, credenciales y cuotas de Neon.

El Dockerfile raíz incorpora el frontend compilado en los recursos estáticos del backend. `config.json` usa `/api` en todos los entornos. Este documento prepara el despliegue; no crea cuentas ni publica una instancia automáticamente.
