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
(JDBC URL `jdbc:h2:mem:testdb`, user `sa`, empty password).

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
