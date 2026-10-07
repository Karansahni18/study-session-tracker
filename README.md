Study Session Tracker

A Spring Boot REST API for recording and managing study sessions. The application uses JWT-based authentication so that each user can access only their own study-session data.

## Features

- User registration and login with stateless JWT authentication
- Protected APIs for creating, viewing, updating, and deleting study sessions
- Per-user data isolation for authenticated requests
- Request validation and centralized API error handling
- MySQL persistence with Spring Data JPA and Hibernate
- Pagination for efficient retrieval of study-session records
- Unit tests for application logic using JUnit 5 and Mockito

## Tech Stack

- Java
- Spring Boot and Spring MVC
- Spring Security and JWT
- Spring Data JPA and Hibernate
- MySQL
- Maven
- JUnit 5 and Mockito

## Project Structure

```text
src/
├── main/
│   ├── java/com/karan/studysessiontracker/
│   │   ├── controller/    # REST controllers
│   │   ├── dto/           # Request and response objects
│   │   ├── entity/        # JPA entities
│   │   ├── repository/    # Database access layer
│   │   ├── security/      # JWT and Spring Security configuration
│   │   └── service/       # Business logic
│   └── resources/
│       └── application.properties
└── test/                  # Tests
```

## Getting Started

### Prerequisites

- A JDK version supported by the project configuration
- MySQL running locally
- Maven (or use the included Maven wrapper)

### 1. Clone the repository

```bash
git clone https://github.com/Karansahni18/study-session-tracker.git
cd study-session-tracker
```

### 2. Configure the database

Create a MySQL database, then update `src/main/resources/application.properties` with your local database URL, username, and password. Keep credentials out of source control.

### 3. Run the application

On macOS/Linux:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The API starts on the port configured in `application.properties`.

## Testing

On macOS/Linux:

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

## Security Notes

- Authentication uses JWTs; send the token in the `Authorization: Bearer <token>` header for protected endpoints.
- Never commit database passwords, JWT secrets, or other credentials. Use environment variables or a local configuration file that is excluded from Git.

## Future Improvements

- Add API documentation with OpenAPI/Swagger
- Add Docker support for the application and database
- Add session statistics and weekly/monthly progress summaries
- Deploy the API to a cloud platform

## Author

Karan Sahni  
[GitHub](https://github.com/Karansahni18) · [LinkedIn](https://www.linkedin.com/in/karan-sahni-0375a8211/)
