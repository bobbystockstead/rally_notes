# Rally Notes API - Quick Start Guide

## 🚀 Getting Started

Your Ktor + MySQL application is now fully scaffolded and ready to go! This guide will help you start building your API endpoints.

## ✅ What's Already Built

### Infrastructure
- ✅ **Database Configuration**: HikariCP connection pooling with MySQL
- ✅ **HTTP Framework**: Ktor with CORS, headers, OpenAPI/Swagger support
- ✅ **Security**: CSRF protection configured
- ✅ **Monitoring**: Health checks, metrics, and request logging
- ✅ **Serialization**: JSON support via kotlinx.serialization
- ✅ **Error Handling**: Centralized exception handling with proper HTTP status codes

### Repositories (Data Access Layer)
Complete repositories for all your database tables:

| Repository | Purpose | Methods |
|---|---|---|
| `RallyRepository` | Rally competitions | `findAll()`, `findById()`, `create()`, `update()`, `delete()` |
| `DriverRepository` | Rally drivers | CRUD operations + driver number |
| `TeamRepository` | Rally teams | CRUD + relationships to driver/co-driver/car/manufacturer |
| `StageRepository` | Rally stages | CRUD + distance tracking |
| `CarRepository` | Rally cars | CRUD + model lookup |
| `ManufacturerRepository` | Car manufacturers | CRUD operations |
| `ModelRepository` | Car models | CRUD + manufacturer lookup |
| `IntensityRepository` | Driving intensity levels | CRUD (light/medium/hard) |
| `WarningRepository` | Driving warnings/hazards | CRUD operations |
| `TipRepository` | Driving tips/techniques | CRUD operations |
| `CallRepository` | Stage driving instructions | CRUD + stage lookup (ordered) |
| `RallyToTeamRepository` | Rally team assignments | Create/read/delete |
| `RallyStageMapRepository` | Rally stage ordering | Create/read/delete/update |

### Models
Complete serializable models with request/response DTOs for all entities.

## 🔧 Building Your First Feature

### Step 1: Add Example Routes

The `Routing.kt` file currently has Rally endpoints. Let's add Team endpoints:

```kotlin
// Add this to Routing.kt in the routing { } block
val teamRepository = TeamRepository()

route("/api/teams") {
    get {
        try {
            val teams = teamRepository.findAll()
            call.respond(ApiResponse(success = true, data = teams))
        } catch (e: Exception) {
            this@configureRouting.log.error("Error fetching teams", e)
            call.respond(HttpStatusCode.InternalServerError, 
                ApiResponse<String>(success = false, error = "Failed to fetch teams"))
        }
    }

    post {
        try {
            val request = call.receive<CreateTeamRequest>()
            val id = teamRepository.create(request.name, request.driverId, request.coDriverId, 
                request.carId, request.manufacturerId)
            val team = teamRepository.findById(id)
            call.respond(HttpStatusCode.Created, ApiResponse(success = true, data = team))
        } catch (e: Exception) {
            this@configureRouting.log.error("Error creating team", e)
            call.respond(HttpStatusCode.InternalServerError, 
                ApiResponse<String>(success = false, error = "Failed to create team"))
        }
    }

    get("/{id}") {
        try {
            val id = call.parameters["id"]?.toIntOrNull() 
                ?: throw IllegalArgumentException("Invalid team id")
            val team = teamRepository.findById(id)
            call.respond(ApiResponse(success = true, data = team))
        } catch (e: RepositoryException.NotFoundException) {
            call.respond(HttpStatusCode.NotFound, 
                ApiResponse<String>(success = false, error = e.message))
        } catch (e: Exception) {
            this@configureRouting.log.error("Error fetching team", e)
            call.respond(HttpStatusCode.InternalServerError, 
                ApiResponse<String>(success = false, error = "Failed to fetch team"))
        }
    }

    put("/{id}") {
        try {
            val id = call.parameters["id"]?.toIntOrNull() 
                ?: throw IllegalArgumentException("Invalid team id")
            val request = call.receive<UpdateTeamRequest>()
            val existing = teamRepository.findById(id)
            
            val updated = teamRepository.update(
                id,
                request.name ?: existing.name,
                request.driverId ?: existing.driverId,
                request.coDriverId ?: existing.coDriverId,
                request.carId ?: existing.carId,
                request.manufacturerId ?: existing.manufacturerId
            )
            
            if (updated) {
                val team = teamRepository.findById(id)
                call.respond(ApiResponse(success = true, data = team))
            } else {
                call.respond(HttpStatusCode.NotFound, 
                    ApiResponse<String>(success = false, error = "Team not found"))
            }
        } catch (e: RepositoryException.NotFoundException) {
            call.respond(HttpStatusCode.NotFound, 
                ApiResponse<String>(success = false, error = e.message))
        } catch (e: Exception) {
            this@configureRouting.log.error("Error updating team", e)
            call.respond(HttpStatusCode.InternalServerError, 
                ApiResponse<String>(success = false, error = "Failed to update team"))
        }
    }

    delete("/{id}") {
        try {
            val id = call.parameters["id"]?.toIntOrNull() 
                ?: throw IllegalArgumentException("Invalid team id")
            val deleted = teamRepository.delete(id)
            
            if (deleted) {
                call.respond(ApiResponse(success = true, data = "Team deleted successfully"))
            } else {
                call.respond(HttpStatusCode.NotFound, 
                    ApiResponse<String>(success = false, error = "Team not found"))
            }
        } catch (e: Exception) {
            this@configureRouting.log.error("Error deleting team", e)
            call.respond(HttpStatusCode.InternalServerError, 
                ApiResponse<String>(success = false, error = "Failed to delete team"))
        }
    }
}
```

### Step 2: Test Your Endpoint

```bash
# Build
./gradlew build

# Run
./gradlew run

# In another terminal, test the endpoint:
curl http://localhost:8080/api/teams

# Create a team
curl -X POST http://localhost:8080/api/teams \
  -H "Content-Type: application/json" \
  -d '{"name":"Team Awesome", "driverId":1, "coDriverId":2}'

# Get team by ID
curl http://localhost:8080/api/teams/1

# Update team
curl -X PUT http://localhost:8080/api/teams/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Team Updated"}'

# Delete team
curl -X DELETE http://localhost:8080/api/teams/1
```

## 📚 Complete Routing Pattern

All routes follow this pattern. Here's how to add routes for each entity:

```kotlin
// Template for any entity
route("/api/entity-plural") {
    val repository = EntityRepository()
    
    get { /* list all */ }
    post { /* create */ }
    get("/{id}") { /* get by id */ }
    put("/{id}") { /* update */ }
    delete("/{id}") { /* delete */ }
}
```

## 🌐 API Documentation

Once running, access:
- **Swagger UI**: `http://localhost:8080/openapi/swagger.html`
- **OpenAPI Schema**: `http://localhost:8080/openapi`
- **Health Check**: `http://localhost:8080/health`

## 🗄️ Database Connection

Your database credentials are in `src/main/resources/application.yaml`:

```yaml
database:
  host: localhost
  port: 3306
  name: rally_notes
  username: api_access
  password: "api_user"
```

Change them to match your actual database setup.

## 📦 Key Files to Modify

| File | Purpose |
|---|---|
| `Routing.kt` | Add your API endpoints here |
| `models/Models.kt` | Update/add data models |
| `repositories/*.kt` | Modify SQL queries if needed |
| `src/main/resources/application.yaml` | Update configuration |

## 🔍 How to Query Data

### Simple Query
```kotlin
val driver = driverRepository.findById(1)
```

### List Query
```kotlin
val allDrivers = driverRepository.findAll()
```

### Create with Generated ID
```kotlin
val newDriverId = driverRepository.create("John Doe", 1)
val newDriver = driverRepository.findById(newDriverId)
```

### Update
```kotlin
val success = driverRepository.update(1, "Jane Doe", 2)
```

### Delete
```kotlin
val deleted = driverRepository.delete(1)
```

### Complex Query (with Connection)
For complex queries not covered by repository methods:

```kotlin
repository.withConnection { conn ->
    conn.createStatement().use { stmt ->
        val result = stmt.executeQuery("SELECT * FROM driver WHERE number > 50")
        // Process result...
    }
}
```

## 🚨 Error Handling

All exceptions are automatically handled and converted to proper HTTP responses:

- **NotFoundException** → 404 Not Found
- **ValidationException** → 400 Bad Request
- **DataAccessException** → 500 Internal Server Error
- Other exceptions → 500 Internal Server Error

Example:
```kotlin
try {
    val team = teamRepository.findById(999)
} catch (e: RepositoryException.NotFoundException) {
    // Automatically returns 404 via StatusPages
}
```

## 🔒 Security Notes

**⚠️ Current Implementation**: Uses basic string escaping for SQL injection prevention.

**For Production**: Update to use PreparedStatements:

```kotlin
// Example PreparedStatement usage
withConnection { conn ->
    conn.prepareStatement("SELECT * FROM driver WHERE driver_id = ?").use { stmt ->
        stmt.setInt(1, id)
        stmt.executeQuery()
    }
}
```

## 📋 Next Steps

1. **Add all endpoints** for each repository following the pattern above
2. **Create services layer** if you need business logic
3. **Add authentication** (JWT, OAuth2)
4. **Add input validation** before database operations
5. **Create integration tests** with a test database
6. **Deploy** to your preferred cloud platform

## 🆘 Troubleshooting

### Database Connection Fails
- Verify MySQL is running
- Check credentials in `application.yaml`
- Ensure database schema exists
- Run `src/main/resources/create_rally.sql` to create tables

### Endpoints Return 500 Error
- Check application logs
- Verify SQL syntax in repository
- Ensure data types match model definitions

### Build Fails
- Run `./gradlew clean build`
- Check for compilation errors in Kotlin files
- Verify all imports are correct

## 📖 References

- [Ktor Documentation](https://ktor.io/docs/)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [MySQL JDBC](https://dev.mysql.com/doc/connector-j/en/)
- [HikariCP Documentation](https://github.com/brettwooldridge/HikariCP)

---

**Your API is ready!** Start adding endpoints and building your rally management system! 🏁

