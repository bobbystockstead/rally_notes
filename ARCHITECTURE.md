# Rally Notes API - Ktor + MySQL Architecture

## Project Overview

This is a professional Ktor-based REST API for managing rally competition data with a MySQL backend. The project demonstrates best practices for building a production-ready Ktor application with manual SQL query management and a clean layered architecture.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    HTTP Layer (Ktor)                            │
│  - Routing (REST Endpoints)                                     │
│  - Request/Response Handling                                    │
│  - Error Handling & Status Pages                                │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    Service/Business Layer                        │
│  - Business Logic                                               │
│  - Validation                                                   │
│  - Orchestration                                                │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    Repository Layer                              │
│  - Data Access Objects (DAO)                                    │
│  - Raw SQL Query Execution                                      │
│  - Result Mapping to Domain Models                              │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    Database Layer                                │
│  - MySQL Database                                               │
│  - Connection Pooling (HikariCP)                                │
│  - Transaction Management                                       │
└─────────────────────────────────────────────────────────────────┘
```

## Project Structure

```
src/
├── main/
│   ├── kotlin/
│   │   ├── Application.kt              # Application entry point & module configuration
│   │   ├── HTTP.kt                     # HTTP plugins (CORS, headers, OpenAPI/Swagger)
│   │   ├── Security.kt                 # Security plugins (CSRF protection)
│   │   ├── Monitoring.kt               # Monitoring (metrics, health checks, logging)
│   │   ├── Serialization.kt            # JSON serialization configuration
│   │   ├── Routing.kt                  # REST API endpoint definitions
│   │   ├── database/
│   │   │   └── Database.kt             # Database initialization & HikariCP configuration
│   │   ├── repositories/
│   │   │   ├── BaseRepository.kt       # Base repository interface with common CRUD operations
│   │   │   └── NoteRepository.kt       # Repository implementation for Rally entity (rename as needed)
│   │   └── models/
│   │       └── Models.kt               # Serializable domain models and DTOs
│   └── resources/
│       ├── application.yaml            # Application configuration (database, ports)
│       └── logback.xml                 # Logging configuration
└── test/
    ├── kotlin/
    │   └── ApplicationTest.kt           # Unit tests
    └── resources/
        └── application.yaml            # Test-specific configuration

build.gradle.kts                        # Gradle build configuration with dependencies
```

## Key Components

### 1. Application.kt
**Purpose**: Entry point for the Ktor application, orchestrates module initialization.

**Key Features**:
- Initializes database connection pool
- Configures HTTP, security, monitoring, serialization, and routing plugins
- Error handling for graceful degradation during initialization

```kotlin
fun Application.module() {
    try {
        configureDatabaseFactory()
    } catch (e: Exception) {
        log.warn("Database initialization skipped or failed: ${e.message}")
    }
    configureHTTP()
    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureRouting()
}
```

### 2. Database.kt
**Purpose**: Manages MySQL connection pooling using HikariCP.

**Key Features**:
- **DatabaseFactory**: Singleton that manages the connection pool
- **DatabaseConfig**: Data class with connection settings (host, port, credentials, pool size)
- **configureDatabaseFactory()**: Ktor plugin that reads config from `application.yaml` and initializes the pool
- Automatic resource cleanup on application shutdown

**Configuration Options** (in `application.yaml`):
```yaml
database:
  host: localhost
  port: 3306
  name: rally_notes
  username: api_access
  password: "api_user"
  pool-size: 20
  skip-init: false  # Set to true in test environments
```

### 3. BaseRepository.kt
**Purpose**: Base interface providing common database operations.

**Methods**:
- `queryForList<T>()`: Execute SELECT queries returning multiple rows
- `queryForObject<T>()`: Execute SELECT queries returning single row or null
- `update()`: Execute INSERT, UPDATE, DELETE statements
- `withConnection()`: Direct connection access for complex operations

**Exception Hierarchy**:
- `RepositoryException.NotFoundException`: Resource not found (HTTP 404)
- `RepositoryException.DataAccessException`: Database operation failed (HTTP 500)
- `RepositoryException.ValidationException`: Invalid input (HTTP 400)

### 4. Repositories (e.g., NoteRepository.kt → RallyRepository.kt)
**Purpose**: Data access layer implementing CRUD operations using raw SQL.

**Pattern**:
- Each table gets its own repository class
- Raw SQL queries defined as constants in companion object
- MapRow functions convert ResultSet to domain models
- Direct SQL string manipulation (basic escaping for quotes)

**Example**:
```kotlin
class RallyRepository : BaseRepository {
    override val dataSource: DataSource get() = DatabaseFactory.getDataSource()
    
    fun findAll(): List<Rally> = queryForList(SELECT_ALL) { mapRowToRally(it) }
    fun findById(id: Int): Rally = queryForObject("$SELECT_BY_ID$id") { mapRowToRally(it) } 
        ?: throw RepositoryException.NotFoundException("Rally with id $id not found")
    fun create(name: String, date: String?): Int = // ... execute insert and return generated ID
    fun update(id: Int, name: String, date: String?): Boolean = // ... return true if updated
    fun delete(id: Int): Boolean = // ... return true if deleted
}
```

### 5. Models.kt
**Purpose**: Serializable domain models and request/response DTOs.

**Structure**:
- Domain Model: `Rally`, `Team`, `Driver`, `Stage`, etc. (map to database tables)
- Create Request: `CreateRallyRequest` (for POST operations)
- Update Request: `UpdateRallyRequest` (optional fields for PUT operations)
- Generic Response: `ApiResponse<T>` (wraps all API responses)

**All models use `@Serializable` from `kotlinx.serialization`** for automatic JSON encoding/decoding by ContentNegotiation plugin.

### 6. Routing.kt
**Purpose**: Define REST API endpoints and orchestrate repository operations.

**Features**:
- Central error handling via `StatusPages` plugin
- RESTful endpoint patterns: `/api/rallies`, `/api/rallies/{id}`
- HTTP methods: GET (list/retrieve), POST (create), PUT (update), DELETE
- Proper HTTP status codes: 200 OK, 201 Created, 400 Bad Request, 404 Not Found, 500 Internal Server Error

**Example Pattern**:
```kotlin
route("/api/rallies") {
    get { /* find all */ }
    post { /* create */ }
    get("/{id}") { /* find by id */ }
    put("/{id}") { /* update */ }
    delete("/{id}") { /* delete */ }
}
```

### 7. HTTP.kt
**Purpose**: Configure HTTP-level features.

**Plugins**:
- **CORS**: Cross-origin requests handling
- **DefaultHeaders**: Adds `X-Engine: Ktor` header to responses
- **OpenAPI**: Serves OpenAPI schema documentation
- **Swagger**: Serves Swagger UI for interactive API exploration

### 8. Security.kt
**Purpose**: Configure security features.

**Plugins**:
- **CSRF**: Cross-Site Request Forgery protection with custom header checks

### 9. Monitoring.kt
**Purpose**: Configure observability and health checks.

**Plugins**:
- **KHealth**: Built-in health check endpoint
- **DropwizardMetrics**: Application metrics collection
- **CallLogging**: HTTP request/response logging

### 10. Serialization.kt
**Purpose**: Configure JSON serialization/deserialization.

**Plugins**:
- **ContentNegotiation**: Automatic JSON encoding based on Content-Type/Accept headers
- Uses `kotlinx.serialization` for JSON format

## Database Schema

Your database includes:
- **Rally** tables: Rally (competitions), Stage (individual stages), RallyStageMap (mapping)
- **Team Management**: Team, Driver, Car, Manufacturer, Model
- **Rally Content**: Call (driving instructions), Intensity, Warning, Tip
- **Relationships**: RallyToTeam (teams in rallies), Foreign keys maintaining data integrity

See `/src/main/resources/create_rally.sql` for complete schema definition.

## SQL Query Management

**Design Decision**: Write all SQL queries manually instead of using ORM/query builders.

**Benefits**:
- Full control over query performance
- Clear understanding of what SQL is executed
- Easy debugging and optimization
- No "magic" SQL generation

**Current Implementation**:
- Queries defined as constants in repository companion objects
- Basic string escaping for quote characters
- Uses JDBC for query execution
- Automatic resource management with `.use { }`

**Future Improvement** (when needed):
- Implement PreparedStatement with parameterized queries for SQL injection prevention
- Create a QueryBuilder utility for complex queries
- Implement pagination helpers

## Configuration

### Main Configuration (application.yaml)
```yaml
ktor:
  application:
    modules:
      - com.racer.ApplicationKt.module
  deployment:
    port: 8080

database:
  host: localhost
  port: 3306
  name: rally_notes
  username: api_access
  password: "api_user"
  pool-size: 20
```

### Environment Variables
Can be overridden with environment variables following Ktor's convention:
- `KTOR_DATABASE_HOST`
- `KTOR_DATABASE_PORT`
- `KTOR_DATABASE_NAME`
- `KTOR_DATABASE_USERNAME`
- `KTOR_DATABASE_PASSWORD`

## Testing

**Current Test Strategy**:
- Simple application initialization test
- No database mocking (integration tests use real database)
- Test configuration disables database initialization to avoid connection failures

**Test Configuration** (`src/test/resources/application.yaml`):
```yaml
ktor:
  deployment:
    environment: test
database:
  skip-init: true
```

**Future Improvements**:
- Add integration tests with test database (Docker MySQL container)
- Mock repositories for unit tests
- Use testcontainers for integration test database

## Running the Application

### Build
```bash
./gradlew build
```

### Run
```bash
./gradlew run
```

### Test
```bash
./gradlew test
```

### Create Fat JAR
```bash
./gradlew buildFatJar
```

### Docker Build
```bash
./gradlew buildImage
```

## API Documentation

### Endpoints Available

#### Rallies
- `GET /api/rallies` - List all rallies
- `GET /api/rallies/{id}` - Get rally by ID
- `POST /api/rallies` - Create new rally
- `PUT /api/rallies/{id}` - Update rally
- `DELETE /api/rallies/{id}` - Delete rally

#### Health
- `GET /health` - Application health status
- `GET /api/health` - Detailed health information

#### Documentation
- `GET /openapi` - OpenAPI schema
- `GET /openapi/swagger.html` - Interactive Swagger UI

## Next Steps - Building Additional Repositories

For each table in your database, you'll need to:

1. **Create a domain model** in `Models.kt`
   ```kotlin
   @Serializable
   data class YourEntity(
       val id: Int,
       val field1: String,
       val field2: String?
   )
   ```

2. **Create request DTOs** in `Models.kt`
   ```kotlin
   @Serializable
   data class CreateYourEntityRequest(val field1: String, val field2: String?)
   
   @Serializable
   data class UpdateYourEntityRequest(val field1: String? = null)
   ```

3. **Create a repository** in `repositories/YourEntityRepository.kt`
   ```kotlin
   class YourEntityRepository : BaseRepository {
       override val dataSource: DataSource get() = DatabaseFactory.getDataSource()
       
       fun findAll(): List<YourEntity> = queryForList(SQL) { mapRow(it) }
       fun findById(id: Int): YourEntity = // ...
       fun create(field1: String, field2: String?): Int = // ...
   }
   ```

4. **Add API routes** in `Routing.kt`
   ```kotlin
   route("/api/your-entities") {
       get { /* list */ }
       post { /* create */ }
       get("/{id}") { /* get */ }
       put("/{id}") { /* update */ }
       delete("/{id}") { /* delete */ }
   }
   ```

## Production Considerations

1. **SQL Injection Prevention**: Move to PreparedStatements with parameterized queries
2. **Connection Security**: Use SSL/TLS for database connections
3. **Authentication**: Add JWT or OAuth2 authentication layer
4. **Authorization**: Implement role-based access control (RBAC)
5. **API Rate Limiting**: Add rate limiting to prevent abuse
6. **Input Validation**: Validate all inputs before database operations
7. **Logging**: Implement structured logging (JSON logs)
8. **Metrics**: Set up Prometheus metrics export
9. **Database Migrations**: Consider adding schema migration tool for production
10. **Caching**: Implement caching layer (Redis) for frequently accessed data

## Dependencies

- **Ktor**: Web framework
- **Kotlin**: Programming language
- **MySQL Connector/J**: JDBC driver for MySQL
- **HikariCP**: Connection pooling
- **Logback**: Logging framework
- **KHealth**: Health check plugin
- **DropwizardMetrics**: Metrics collection
- **kotlinx.serialization**: JSON serialization

## Troubleshooting

### Database Connection Errors
- Verify credentials in `application.yaml`
- Ensure MySQL server is running and accessible
- Check database name exists
- Verify firewall allows connections on port 3306

### 500 Internal Server Errors
- Check application logs for stack traces
- Verify SQL syntax in repository queries
- Ensure database schema matches expectations
- Check data types match model definitions

### Tests Failing to Connect
- Ensure test configuration has `skip-init: true` in `database` section
- Check that test resources configuration file exists

---

**Ready to build your API!** Start by renaming `NoteRepository.kt` to match your first entity and updating the routes.

