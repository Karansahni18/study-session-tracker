# Study Session Tracker

A Spring Boot REST API for logging and managing study sessions. Users register and log in to receive a JWT, and every subject and study session belongs to exactly one user: the API only ever reads or changes the caller's own data.

## Features

- Registration and login with stateless JWT authentication and BCrypt-hashed passwords
- Subjects and study sessions; sessions support create, read (single and paginated list), update, and delete
- Per-user data isolation: lookups, updates, and deletes verify ownership, and a session can only be attached to a subject the caller owns
- Request validation (Bean Validation) and centralized error handling with clear JSON error responses
- MySQL persistence with Spring Data JPA and Hibernate, a composite index on `(user_id, session_date)`, and paginated reads that avoid N+1 queries
- Unit tests for the service layer with JUnit and Mockito
- Interactive API documentation with Swagger UI (OpenAPI), including a JWT Authorize button

## Tech Stack

- Java 21
- Spring Boot 4.1 (Spring MVC, Spring Security, Spring Data JPA, Bean Validation)
- JWT via JJWT 0.12
- Hibernate and MySQL 8
- Maven (wrapper included)
- JUnit 6 (Jupiter) and Mockito 5
- springdoc-openapi 3.0 (Swagger UI)

## Project Structure

```text
src/
  main/
    java/com/karan/studysessiontracker/
      config/       OpenAPI (Swagger) configuration
      controller/   REST controllers (auth, subjects, sessions)
      dto/          request and response objects
      entity/       JPA entities (User, Subject, StudySession)
      exception/    custom exceptions and the global exception handler
      repository/   Spring Data JPA repositories
      security/     JWT utility, JWT filter, Spring Security configuration
      service/      business logic and ownership checks
    resources/
      application.properties
  test/             unit tests for the service layer
```

## Getting Started

### Prerequisites

- JDK 21
- MySQL 8 running locally
- Maven is optional; the Maven wrapper is included

### 1. Clone the repository

```bash
git clone https://github.com/Karansahni18/study-session-tracker.git
cd study-session-tracker
```

### 2. Create the database and a dedicated user

```sql
CREATE DATABASE study_session_tracker;
CREATE USER 'ssta_app'@'localhost' IDENTIFIED BY 'choose-a-strong-password';
GRANT ALL PRIVILEGES ON study_session_tracker.* TO 'ssta_app'@'localhost';
```

Tables are created automatically on first run (`spring.jpa.hibernate.ddl-auto=update`).

### 3. Set environment variables

The application reads its credentials from the environment, so nothing secret lives in the repository or in `application.properties`.

| Variable | Purpose |
|---|---|
| `DB_USERNAME` | MySQL user, for example `ssta_app` |
| `DB_PASSWORD` | Password for that user |
| `JWT_SECRET` | Token signing key: at least 32 characters, 64 random characters recommended |

Windows PowerShell (current session):

```powershell
$env:DB_USERNAME="ssta_app"
$env:DB_PASSWORD="your-password"
$env:JWT_SECRET="your-long-random-secret"
```

macOS/Linux:

```bash
export DB_USERNAME=ssta_app
export DB_PASSWORD=your-password
export JWT_SECRET=your-long-random-secret
```

Ways to generate a secret:

```powershell
-join (1..64 | ForEach-Object { '{0:x}' -f (Get-Random -Maximum 16) })
```

```bash
openssl rand -hex 32
```

### 4. Run the application

On macOS/Linux:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The API starts on `http://localhost:8080`.

## API

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | No | Create an account and receive a token |
| POST | `/api/auth/login` | No | Log in and receive a token |
| POST | `/api/subjects` | Yes | Create a subject |
| GET | `/api/subjects` | Yes | List your subjects |
| POST | `/api/sessions` | Yes | Create a study session |
| GET | `/api/sessions?page=0&size=10` | Yes | List your sessions, newest first (page size capped at 50) |
| GET | `/api/sessions/{id}` | Yes | Get one session |
| PUT | `/api/sessions/{id}` | Yes | Replace a session (all fields required) |
| DELETE | `/api/sessions/{id}` | Yes | Delete a session (returns 204) |

Protected endpoints expect the header `Authorization: Bearer <token>`. Tokens are valid for 24 hours.

### Interactive documentation (Swagger UI)

With the app running, open `http://localhost:8080/swagger-ui/index.html`. To try the protected endpoints:

1. Call `POST /api/auth/register` or `POST /api/auth/login` and copy the `token` value from the response.
2. Click **Authorize**, paste the token (without the `Bearer ` prefix) and confirm.
3. Use **Try it out** on any endpoint.

The raw OpenAPI description is available at `/v3/api-docs`.

### Example requests

`POST /api/auth/register`

```json
{ "username": "demo", "password": "demo12345" }
```

Response:

```json
{ "token": "<jwt>", "username": "demo" }
```

`POST /api/subjects` (with the bearer token)

```json
{ "name": "Java" }
```

`POST /api/sessions` (with the bearer token)

```json
{
  "topic": "Streams",
  "durationMinutes": 45,
  "sessionDate": "2026-10-10",
  "notes": "Practice",
  "subjectId": 1
}
```

### Validation rules

- Username: 3 to 20 characters. Password: 6 to 128 characters.
- Subject name: required, up to 50 characters.
- Session topic: required, up to 100 characters. Duration: a positive number. Date: required. Notes: up to 1000 characters. Subject: required and must belong to the caller.

### Error responses

| Status | When |
|---|---|
| 400 | Validation failure (a field-by-field message map) or a malformed request body |
| 401 | Invalid username or password |
| 403 | The resource belongs to another user, or the request has no valid token (missing, expired, or tampered) |
| 404 | Session or subject not found |
| 405 | HTTP method not supported for the endpoint |
| 409 | Username already taken |

Most errors share this shape:

```json
{ "status": 404, "message": "Session not found", "timestamp": 1791746423032 }
```

Validation errors return the field names with their messages instead:

```json
{ "topic": "Topic is required" }
```

## Testing

On macOS/Linux:

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

The 13 unit tests cover the service layer (ownership checks, registration, login) with mocked repositories and need no database. The generated context-load test starts the whole application, so it needs MySQL running and the environment variables above set.

## Design Notes

- **Stateless authentication.** The server keeps no session. Each request carries a signed JWT that a filter validates, so any server instance can handle any request.
- **Bad tokens.** Malformed, expired, or tampered tokens are treated as unauthenticated instead of raising a server error, so protected endpoints return 403 and public endpoints such as login keep working.
- **Authorization against ID guessing.** Every endpoint that takes an ID loads the record and compares its owner with the caller before doing anything. List queries are filtered by user. Creating or updating a session also verifies the subject belongs to the caller.
- **DTOs instead of entities.** Responses never expose entities, so password hashes and internal relationships cannot leak.
- **N+1 queries.** Listing sessions originally ran one extra query per distinct subject. `@EntityGraph(attributePaths = "subject")` on that repository method fetches the subject in the same query with a join, while the relationship stays lazy everywhere else. Because it is a to-one association, the `LIMIT` for pagination still happens in SQL.
- **Index and ordering.** The composite index on `(user_id, session_date)` matches the list query (filter by user, sort by date). Ordering by date and then id keeps paging deterministic when dates tie.
- **Transactions.** Update and delete run in one transaction, so the ownership check and the write succeed or fail together.
- **Input limits.** Field sizes and the maximum page size are capped to limit oversized requests.

## Known Limitations

- Subjects can be created and listed, but not renamed or deleted.
- Requests without a valid token receive a 403 (in Spring's default JSON shape) rather than a 401.
- Errors generated by the framework itself (for example an unknown URL) use Spring's default JSON shape instead of the shape above.
- The schema is managed with `ddl-auto=update`, which suits development; production should use migrations such as Flyway.
- Mockito prints a harmless dynamic-agent warning when tests run on JDK 21.

## Future Improvements

- Docker support for the application and database
- Update and delete for subjects
- Integration tests for the controllers and security rules
- Session statistics and weekly/monthly progress summaries
- Deployment to a cloud platform

## Author

Karan Sahni  
[GitHub](https://github.com/Karansahni18)
