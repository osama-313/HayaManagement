# Team Management API

[![CI](https://github.com/osama-313/HayaManagement/actions/workflows/ci.yml/badge.svg)](https://github.com/osama-313/HayaManagement/actions/workflows/ci.yml)
![Java 17](https://img.shields.io/badge/Java-17-orange)
![Spring Boot 3.5](https://img.shields.io/badge/Spring%20Boot-3.5-6db33f)

REST API behind the Haya team management app: team members, tasks, time commitments and shared resources, secured with
JWT authentication and role-based access control. The Angular client lives in
[Hayaa-Management](https://github.com/Ya313/Hayaa-Management).

## Highlights

- **Stateless JWT auth with revocation.** Tokens are signed with HMAC-SHA256, carry a unique `jti`, and are tracked in
  the database so logout takes effect immediately. Expired tokens are purged nightly.
- **Role-based authorization.** Administrators manage everything. Members manage only their own tasks and commitments.
  Ownership is derived from the authenticated principal, never from request bodies, so a client cannot act as someone else.
- **DTOs everywhere.** Entities are never serialised, so password hashes cannot leak. Requests are validated with
  Bean Validation and errors come back as [RFC 7807](https://datatracker.ietf.org/doc/html/rfc7807) problem documents with per-field messages.
- **Efficient reads.** Entity graphs avoid N+1 queries, and the dashboard is computed with grouped aggregate queries
  rather than by loading every row.
- **Twelve-factor configuration.** All settings come from environment variables, bound to a validated
  `@ConfigurationProperties` record. The app refuses to start without a JWT secret of at least 256 bits.
- **Self-documenting.** OpenAPI spec and Swagger UI are served by the app itself.
- **Tested.** 38 unit and full-stack integration tests (MockMvc + in-memory database) cover authentication, authorization,
  validation and the business rules.

## Tech stack

Java 17 · Spring Boot 3.5 · Spring Security 6 · Spring Data JPA / Hibernate · PostgreSQL · jjwt · springdoc-openapi · JUnit 5 · Maven

## Quick start

You only need a JDK 17. No database required: the `dev` profile uses an in-memory H2 database and loads a demo team.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

- API: <http://localhost:8080>
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Demo accounts (password `Demo@2024`): `admin` (administrator) and `member` (regular member)

Run the tests with `./mvnw verify`.

### With PostgreSQL via Docker

```bash
cp .env.example .env        # set DB_PASSWORD and JWT_SECRET (openssl rand -base64 48)
docker compose up --build
```

## Configuration

| Variable | Required | Description |
| --- | --- | --- |
| `DB_URL` | yes | JDBC URL, e.g. `jdbc:postgresql://host:5432/teammgmt` |
| `DB_USERNAME` / `DB_PASSWORD` | yes | Database credentials |
| `JWT_SECRET` | yes | Base64 signing key, at least 256 bits (`openssl rand -base64 48`) |
| `JWT_EXPIRATION` | no | Token lifetime, default `24h` |
| `CORS_ALLOWED_ORIGINS` | no | Comma-separated browser origins, default `http://localhost:4200` |
| `ADMIN_USERNAME` / `ADMIN_PASSWORD` / `ADMIN_EMAIL` | no | Creates the first administrator when the database has no users |
| `DEMO_DATA` | no | `true` seeds a demo team into an empty database |
| `JPA_DDL_AUTO` | no | Hibernate schema mode, default `update` |
| `SWAGGER_ENABLED` | no | Set to `false` to switch off the OpenAPI endpoints |
| `PORT` | no | HTTP port, default `8080` |

## API overview

All endpoints except login and health require `Authorization: Bearer <token>`. The full, interactive reference is in Swagger UI.

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| `POST` | `/api/auth/login` | public | Exchange credentials for a token |
| `POST` | `/api/auth/logout` | authenticated | Revoke the current token |
| `GET` | `/api/auth/me` | authenticated | Current user |
| `GET` | `/api/dashboard` | authenticated | Team totals and per-member workload |
| `GET` | `/api/users`, `/api/users/{id}` | authenticated | List / read members |
| `POST` `PUT` `DELETE` | `/api/users[/{id}]` | admin | Manage members |
| `GET` | `/api/tasks?assigneeId=` | authenticated | List tasks, optionally for one member |
| `POST` `PUT` `DELETE` | `/api/tasks[/{id}]` | admin, or the assignee | Manage tasks |
| `GET` `POST` | `/api/commitments` | authenticated | List / record the caller's commitments |
| `PUT` `DELETE` | `/api/commitments/{id}` | admin, or the owner | Manage commitments |
| `GET` `POST` | `/api/resources` | authenticated | List / share links |
| `PUT` `DELETE` | `/api/resources/{id}` | admin | Manage links |
| `GET` | `/actuator/health` | public | Liveness / readiness |

## Project layout

```
src/main/java/com/haya
├── config/        security chain, CORS, OpenAPI, typed properties, startup data
├── controller/    thin REST controllers
├── dto/           request / response records with validation rules
├── entity/        JPA entities
├── exception/     domain exceptions and the global problem-details handler
├── repository/    Spring Data repositories (entity graphs, aggregate queries)
├── security/      JWT service and filter, authenticated principal
└── service/       business rules and authorization decisions
```

## Deploying

The included multi-stage `Dockerfile` produces a small, non-root image that listens on `$PORT`, so it runs unchanged on
Render, Railway, Fly.io or any container host. Point `DB_URL` at a managed PostgreSQL, set `JWT_SECRET` and
`CORS_ALLOWED_ORIGINS` to your frontend's URL, and terminate TLS at the platform's router.

## Notes on the database

Column and table names are unchanged from the first version of the project so existing databases keep working;
Hibernate adds the new `expires_at` column to the `token` table on first start. For a larger production system,
switch `JPA_DDL_AUTO` to `validate` and manage the schema with Flyway or Liquibase.
