# ucotestapi · RegistrarNotaAEstudiante

Un docente activo le registra a un estudiante activo de su grupo una calificación de 0.00 a 5.00 (máximo 2
decimales) en una evaluación de ese grupo, si el grupo está vigente, se está en fechas de registro y el estudiante
aún no tiene nota en ella. Arquitectura hexagonal con el empaquetado de la clase (`dominio`, `servicios`,
`infraestructura`, `inicializador`, `transversal`).

La especificación completa (decisiones A-n, escenarios C-n) está en
`~/Work/ucotestapi/specs/registrar-nota-a-estudiante/espec/ESPEC-registrar-nota-a-estudiante.md`.

## Requisitos

- Java 27 (Temurin), Docker con Compose v2. Maven va incluido (`./mvnw`).
- Node 24 LTS solo si quieres trabajar el frontend fuera de Docker.

## Modo demo: todo en Docker

```sh
./mvnw -DskipTests jib:dockerBuild        # imagen ucotestapi:local del backend
docker compose --profile demo up -d --wait
```

Abre <http://localhost:8080> e inicia sesión con un usuario de la clase simulada. **Contraseña de demo de todos:
`ucotest`** (no es un secreto: es la clave pública de la demo; vive en el vault como `clave-demo`).

| Usuario | Rol |
|---|---|
| `carlos` | DOCENTE de «Arquitectura de Software 01» |
| `ana`, `andres`, `beatriz`, `david`, `elena` | ESTUDIANTE matriculados en el grupo |

Registra 4.35 a Ana en «Parcial 1»: la respuesta es 201 con el mensaje de éxito. Un segundo intento responde 422
`M-NOTA-R06` («El estudiante ya tiene una calificación en esta evaluación»).

Para empezar de cero (base de datos, realm y secretos nuevos): `docker compose --profile demo down -v`.

## Modo desarrollo: backend desde Eclipse

```sh
docker compose up -d --wait                       # infraestructura sin el backend
export SPRING_CLOUD_VAULT_TOKEN="$(docker compose run --rm --no-deps --entrypoint cat vault-init \
  /run/secretos/app/spring.cloud.vault.token)"
./mvnw spring-boot:run                            # o Run As → Spring Boot App con esa variable
```

El backend escucha en `http://localhost:8081` (`PORT` lo cambia) y el API Gateway le reenvía `/api` cuando no hay
contenedor `app`. El frontend se sirve igual en <http://localhost:8080>; para trabajarlo con recarga en caliente:
`cd frontend && npm ci && npm run dev` (Vite en 5173 reenvía `/api` y `/auth` al WAF). Si usas Vite, agrega
`http://localhost:5173/*` a los redirect URIs del cliente `ucotest-frontend` en Keycloak.

## Cómo está armado

```
navegador → waf (ModSecurity + OWASP CRS, :8080) → api-gateway (Nginx) ─┬→ frontend (Vue 3 + Vite)
                                                                        ├→ app (Spring Boot) → postgres
                                                                        └→ keycloak (realm ucotest)
vault (OpenBao) ← vault-init: genera los secretos al azar y entrega a cada servicio solo lo suyo
```

- **Secretos:** ninguno en el repositorio. `vault-init` genera las contraseñas de Postgres, del administrador de
  Keycloak y de RabbitMQ, las guarda en OpenBao y deja a cada contenedor solo su archivo en un volumen en memoria. La
  app recibe un token de solo lectura y lee `spring.datasource.*` del vault con Spring Cloud Vault. En Azure se
  cambia OpenBao por Key Vault con los mismos nombres de secreto.
- **URL pública:** `UCOTEST_URL_PUBLICA` (por defecto `http://localhost:8080`) fija el issuer de Keycloak, los
  redirect URIs del realm y el issuer que valida el backend.
- **Base de datos:** Hibernate crea el esquema (`ddl-auto=update`); el inicializador siembra la clase simulada si
  la base está vacía, con el periodo del grupo relativo al día de arranque.

## Pruebas

```sh
./mvnw verify
```

Dominio con pruebas unitarias; caso de uso, adaptadores y web contra Postgres real con Testcontainers (sin dobles de
persistencia). Los escenarios C-1..C-21 de la ESPEC tienen su prueba; la matriz está en la ESPEC §9.

## CI

`.github/workflows/`: `ci.yml` (backend, frontend, compose), `codeql.yml` (Java y TypeScript) y `dependencias.yml`
(dependency review y Trivy sobre las imágenes del compose). `dependabot.yml` propone actualizaciones.
