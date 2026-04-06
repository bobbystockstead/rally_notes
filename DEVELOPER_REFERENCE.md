# Developer Quick Reference

**Project**: Rally Notes API  
**Framework**: Ktor 3.4.2  
**Database**: MySQL with HikariCP  
**Language**: Kotlin 2.3.0  
**JDK**: Java 21  

## 🚀 Quick Start

```bash
# Run the application
./gradlew run

# API available at: http://localhost:8080
# Swagger UI: http://localhost:8080/openapi/swagger.html
```

## 📂 File Map

| File | When to Edit |
|------|-------------|
| `Routing.kt` | Add new API endpoints |
| `models/Models.kt` | Add new domain models |
| `repositories/*.kt` | Add new repositories or modify SQL |
| `application.yaml` | Database credentials, ports |
| `Security.kt` | Add authentication |

## 🔌 Adding a New Entity Endpoint

### 1. Ensure Repository Exists
```kotlin
// repositories/EntityRepository.kt exists? ✓
```

### 2. Add Models (if needed)
```kotlin
// In models/Models.kt
@Serializable
data class Entity(val id: Int, val name: String)

@Serializable
data class CreateEntityRequest(val name: String)

@Serializable
data class UpdateEntityRequest(val name: String? = null)
```

### 3. Add Routes
```kotlin
// In Routing.kt
route("/api/entities") {
    val repository = EntityRepository()
    
    get {
        val entities = repository.findAll()
        call.respond(ApiResponse(success = true, data = entities))
    }
    
    post {
        val request = call.receive<CreateEntityRequest>()
        val id = repository.create(request.name)
        call.respond(HttpStatusCode.Created, 
            ApiResponse(success = true, data = repository.findById(id)))
    }
}
```

## 🗄️ Repository Pattern

Every repository follows this pattern:

```kotlin
class XyzRepository : BaseRepository {
    override val dataSource: DataSource get() = DatabaseFactory.getDataSource()
    
    companion object {
        // SQL constants
        private const val SELECT_ALL = "SELECT ... FROM xyz"
        private const val INSERT = "INSERT INTO xyz ..."
        
        // Result mapper
        private fun mapRowToXyz(rs: ResultSet): Xyz = Xyz(...)
    }
    
    // CRUD methods
    fun findAll(): List<Xyz> = queryForList(SELECT_ALL) { mapRowToXyz(it) }
    fun findById(id: Int): Xyz = queryForObject(...) { mapRowToXyz(it) } 
        ?: throw RepositoryException.NotFoundException(...)
    fun create(...): Int = withConnection { /* generate and return ID */ }
    fun update(...): Boolean = update(sql) > 0
    fun delete(id: Int): Boolean = update("DELETE...") > 0
}
```

## 🚦 HTTP Status Codes Used

| Status | When | Example |
|--------|------|---------|
| 200 OK | Successful GET/PUT/DELETE | List, update, delete |
| 201 Created | Successful POST | Create new resource |
| 400 Bad Request | Validation error | Missing required fields |
| 404 Not Found | Resource doesn't exist | GET non-existent ID |
| 500 Internal Server Error | Database error | Connection failed |

## 💾 Database Operations

### Query Examples

```kotlin
// Get all
val items = itemRepository.findAll()

// Get one
val item = itemRepository.findById(1)

// Create
val newId = itemRepository.create("name")

// Update
itemRepository.update(1, "new name")

// Delete
itemRepository.delete(1)

// Complex query
itemRepository.withConnection { conn ->
    val stmt = conn.createStatement()
    val result = stmt.executeQuery("SELECT * FROM item WHERE name LIKE '%test%'")
    // Process result
}
```

### Exception Handling

```kotlin
try {
    val item = itemRepository.findById(999)
} catch (e: RepositoryException.NotFoundException) {
    // Item not found - will be converted to 404
} catch (e: RepositoryException.DataAccessException) {
    // Database error - will be converted to 500
}
```

## 🔐 Common Security Patterns

### Input Validation
```kotlin
val request = call.receive<CreateItemRequest>()
if (request.name.isEmpty()) {
    throw RepositoryException.ValidationException("Name cannot be empty")
}
```

### Authentication (Future)
```kotlin
authenticate {
    route("/api/items") {
        get { /* only authenticated users */ }
    }
}
```

## 📝 API Response Format

All responses use this format:

```json
{
  "success": true,
  "data": { /* actual response */ },
  "error": null
}
```

Error response:
```json
{
  "success": false,
  "data": null,
  "error": "Error message"
}
```

## 🛠️ Configuration

### Environment Variables
```bash
export KTOR_DATABASE_HOST=localhost
export KTOR_DATABASE_USERNAME=api_access
export KTOR_DATABASE_PASSWORD=api_user
./gradlew run
```

### application.yaml
```yaml
database:
  host: localhost
  port: 3306
  name: rally_notes
  username: api_access
  password: "api_user"
  pool-size: 20
```

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests ApplicationTest

# Run with logging
./gradlew test -i
```

## 📊 Monitoring & Debugging

### Check Logs
```bash
# Application logs appear in console when running with ./gradlew run
# Look for lines starting with [main]
```

### Health Check
```bash
curl http://localhost:8080/health
```

### Metrics
```bash
curl http://localhost:8080/metrics
```

## 🏗️ Repository Instances

Current repositories (instantiate in Routing.kt):

```kotlin
val rallyRepository = RallyRepository()
val driverRepository = DriverRepository()
val teamRepository = TeamRepository()
val stageRepository = StageRepository()
val carRepository = CarRepository()
val manufacturerRepository = ManufacturerRepository()
val modelRepository = ModelRepository()
val intensityRepository = IntensityRepository()
val warningRepository = WarningRepository()
val tipRepository = TipRepository()
val callRepository = CallRepository()
val rallyToTeamRepository = RallyToTeamRepository()
val rallyStageMapRepository = RallyStageMapRepository()
```

## 🔍 Debugging SQL

If you need to debug SQL queries:

```kotlin
// Add this in repository before executing:
println("Executing: $sql")

// Or use Logback:
logger.debug("Executing query: {}", sql)
```

## 📚 Documentation Files

| File | Contains |
|------|----------|
| `README.md` | Generated project info |
| `ARCHITECTURE.md` | System design deep dive |
| `QUICKSTART.md` | Hands-on tutorial |
| `PROJECT_SUMMARY.md` | Complete overview |
| `This file` | Quick reference |

## ⚡ Performance Tips

1. **Use Connection Pool**: Already configured (HikariCP)
2. **Index Frequently Queried Fields**: Check create_rally.sql
3. **Avoid N+1 Queries**: Batch related queries
4. **Use Prepared Statements**: For SQL injection prevention
5. **Add Pagination**: For large result sets

## 🐛 Common Issues & Fixes

| Issue | Solution |
|-------|----------|
| Port already in use | Change port in application.yaml |
| Database connection fails | Check credentials and MySQL running |
| 404 on endpoints | Verify route added to Routing.kt |
| SQL errors | Check SQL syntax in repository |
| Model deserialization fails | Verify model has @Serializable |
| Test fails | Ensure test config has skip-init: true |

## 📞 Key Contacts

- Ktor Support: https://ktor.io/docs/
- Kotlin Docs: https://kotlinlang.org/docs/
- MySQL Docs: https://dev.mysql.com/

## ✅ Pre-Commit Checklist

Before committing:

- [ ] Code compiles: `./gradlew build`
- [ ] Tests pass: `./gradlew test`
- [ ] No debug println() statements left
- [ ] SQL queries reviewed for injection
- [ ] Models have @Serializable
- [ ] Error messages are clear
- [ ] No hardcoded credentials

## 🚀 Deployment Checklist

Before deploying to production:

- [ ] Change database credentials
- [ ] Set appropriate pool-size
- [ ] Enable authentication
- [ ] Review CORS origins
- [ ] Add rate limiting
- [ ] Set up monitoring
- [ ] Configure logging
- [ ] Test with production data
- [ ] Create backup strategy
- [ ] Document API changes

---

**Save this page!** Reference it while developing.

