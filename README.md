# Spring Boot Todo API

A small CRUD REST API for managing a todo list, built with Spring Boot. Todos are
persisted to an in-memory H2 database.

## Features

- Create, read, update and delete todos
- Filter todos by completion status (`?completed=true`)
- Mark a todo as complete/incomplete via a PATCH endpoint
- Bean Validation on input
- In-memory H2 database with console

## Tech Stack

- Java 17
- Spring Boot (Web MVC, Data JPA, Validation)
- H2 (in-memory database)

## Running

```bash
./mvnw spring-boot:run
```

App starts on http://localhost:8080. H2 console: http://localhost:8080/h2-console
(user `sa`, empty password). The database name is generated on each start, so copy the
JDBC URL from the startup log line `H2 console available at '/h2-console'. Database
available at 'jdbc:h2:mem:...'`.

## API

| Method | Path                    | Description                          |
|--------|-------------------------|--------------------------------------|
| GET    | `/api/todos`            | List todos (`?completed=true/false`) |
| GET    | `/api/todos/{id}`       | Get a single todo                    |
| POST   | `/api/todos`            | Create a todo                        |
| PUT    | `/api/todos/{id}`       | Update a todo                        |
| PATCH  | `/api/todos/{id}/completed?value=true` | Set completion status |
| DELETE | `/api/todos/{id}`       | Delete a todo                        |

### Example

```bash
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "Write README", "description": "Document the API", "completed": false}'
```

`description` (up to 2000 characters) and `completed` are optional; `completed` defaults
to `false`.

### Errors

Errors are returned as RFC 7807 `ProblemDetail` JSON:

- `400` for invalid input, with an `errors` object mapping each rejected field to its message
- `404` when no todo has the given id
