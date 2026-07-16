# Taskflow REST API

A task management REST API built with Spring Boot.

## Tech Stack

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT
- Swagger (Springdoc OpenAPI)
- JUnit 5
- Mockito
- Docker

## Features

- JWT authentication
- Task CRUD
- Search, pagination and sorting
- Bean Validation
- Global exception handling

## Business Rules

Tasks follow this workflow:

```text
TODO → IN_PROGRESS → DONE → ARCHIVED
```

Rules:

- A task must have a due date before it can be marked as `DONE`.
- Completed or archived tasks cannot be edited.
- Only completed tasks can be archived.
- Each user can have up to 100 tasks.
- Due dates cannot be in the past.

Invalid operations return an appropriate HTTP status (`400` or `409`).

## Run

```bash
docker compose up -d
mvn spring-boot:run
```

Swagger: `http://localhost:8080/swagger-ui.html`
