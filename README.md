# Babetta Garáž

[![CI](https://github.com/K3vlar159/babettagaraz/actions/workflows/ci.yml/badge.svg)](https://github.com/K3vlar159/babettagaraz/actions/workflows/ci.yml)

A community platform for owners of the Babetta moped: a marketplace for parts and mopeds, and a blog with repair guides. This repository contains the backend REST API.

**Live:** [babettagaraz.sk](https://babettagaraz.sk) (opens the Swagger UI)

## Tech stack

- **Java 21**, **Spring Boot 4.1** (Web MVC, Data JPA, Validation)
- **Spring Security** with stateless **JWT** authentication (jjwt)
- **PostgreSQL 16**, schema managed by **Flyway**
- **springdoc-openapi**: Swagger UI with a Bearer token *Authorize* button
- **JUnit 5**, **Mockito**, **MockMvc**
- **Docker** (multi-stage build), **Docker Compose**
- **GitHub Actions**: build and tests on every push
- **Cloudflare Tunnel**: public HTTPS access to a self-hosted server

## Features

- Registration and login, which returns a JWT
- Listings: create, read, update, archive
- Blog posts: create, read, update, archive
- Only the author can edit or archive their own content; anyone else gets `403`
- Archived listings are hidden and return `404`
- Request validation with clear error responses (`400`)
- Interactive API documentation in Swagger UI

## API overview

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | – | Create an account (`201`, `409` if email or username is taken) |
| POST | `/api/auth/login` | – | Log in and receive a JWT |
| GET | `/api/listings` | – | List active listings |
| GET | `/api/listings/{id}` | – | Listing detail |
| POST | `/api/listings` | JWT | Create a listing (`201`) |
| PUT | `/api/listings/{id}` | JWT, author | Update a listing |
| PATCH | `/api/listings/{id}/archive` | JWT, author | Archive a listing (`204`) |
| GET | `/api/blog` | – | List published posts |
| GET | `/api/blog/{id}` | – | Post detail |
| POST | `/api/blog` | JWT | Create a post (`201`) |
| PUT | `/api/blog/{id}` | JWT, author | Update a post |
| PATCH | `/api/blog/{id}/archive` | JWT, author | Archive a post (`204`) |

Full documentation, including request and response schemas, is in the [Swagger UI](https://babettagaraz.sk).

## Design decisions

- **The author comes from the token, never from the request body.** Ownership is checked on the server, so a client cannot create or edit content in someone else's name.
- **Same `401` for a wrong username and a wrong password**, so the API does not reveal which accounts exist.
- **Soft delete:** content is archived by changing its status, not deleted from the database.
- **One place for error handling:** a `@RestControllerAdvice` maps exceptions to `400`, `401`, `403`, `404` and `409` responses.
- **Flyway owns the schema.** Hibernate only validates it (`ddl-auto=validate`).
- **Stateless security:** no server-side sessions, so CSRF protection is disabled for the API.

## Deployment

```
Browser ──HTTPS──> Cloudflare ──Tunnel──> cloudflared ──HTTP──> Spring Boot ──> PostgreSQL
                                          └──────── home server, Docker Compose ────────┘
```

- **Hosting:** runs on a self-hosted Linux server. The app and PostgreSQL run as separate containers (`docker-compose.prod.yml`), with data in a named volume.
- **Secrets:** read from environment variables through the `prod` profile. They live in an `.env` file on the server and are never committed. `.env.example` lists the required keys.
- **Public access:** goes through a Cloudflare Tunnel, so no ports are opened on the router.
- **Behind the proxy:** HTTPS ends at Cloudflare. The app reads `X-Forwarded-*` headers to know the original scheme.
- **Edge protection:** a Cloudflare WAF rule blocks common vulnerability scans (`/.env`, `/.git`, `*.php`, …), and a rate limit protects `/api/auth/*`.

## Running locally

Requirements: JDK 21 and Docker.

```bash
git clone https://github.com/K3vlar159/babettagaraz.git
cd babettagaraz
docker compose up -d        # PostgreSQL 16 on localhost:5432
./mvnw spring-boot:run      # the "local" profile is active by default
```

Open [http://localhost:8080](http://localhost:8080) to reach the Swagger UI. To call a protected endpoint:

1. Register with `POST /api/auth/register`.
2. Log in with `POST /api/auth/login` and copy the token from the response.
3. Click **Authorize** and paste the token.

## Tests

```bash
docker compose up -d
./mvnw verify
```

- **Unit tests** for services (Mockito) and JWT handling
- **Controller tests** with `@WebMvcTest` and MockMvc, run with the real security configuration (`200`, `302`, `400`, `404`)
- **Context test** against a real PostgreSQL database

The same `mvn verify` runs in GitHub Actions on every push, with PostgreSQL as a service container.

## Project structure

```
src/main/java/sk/babettagaraz
├── domain
│   ├── listing      # listings: entity, repository, service, controller, DTOs
│   ├── blog         # blog posts and tags
│   ├── user         # users, registration, login
│   └── forum        # data model for the upcoming forum
├── security         # JWT service and authentication filter
├── config           # Spring Security and OpenAPI configuration
├── common           # exceptions and the global exception handler
└── web              # root redirect to Swagger UI
```

## Roadmap

- [ ] Angular frontend with demo data
- [ ] Forum: categories, threads, nested replies, moderation
- [ ] Role-based access for moderators and admins
- [ ] Integration tests with Testcontainers, coverage report with JaCoCo
- [ ] Health checks, automated deployment, daily database backups

## Author

**Dávid Jerguš**: [LinkedIn](https://www.linkedin.com/in/dj526174256/) · [GitHub](https://github.com/K3vlar159)