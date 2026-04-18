package com.racing.routes

import com.racing.data.Crew
import com.racing.data.ValidationErrorResponse
import com.racing.data.ValidationIssue
import com.racing.db.*
import com.racing.routes.docs.attachCrewOpenApi
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Location
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("CrewRoutes")

fun Route.crewRoutes(repo: CrewRepository, driverRepo: DriverRepository, codriverRepo: CodriverRepository, carRepo: CarRepository, teamRepo: TeamRepository) {

    fun validateResources(crew: Crew): List<ValidationIssue> {
        val issues = mutableListOf<ValidationIssue>()

        if (crew.driver_id == null) {
            issues += ValidationIssue("driver_id", "Driver is required")
        } else if (driverRepo.getById(crew.driver_id) == null) {
            issues += ValidationIssue("driver_id", "Driver does not exist")
        }
        if (crew.codriver_id == null) {
            issues += ValidationIssue("codriver_id", "Codriver is required")
        } else if (codriverRepo.getById(crew.codriver_id) == null) {
            issues += ValidationIssue("codriver_id", "Codriver does not exist")
        }

        if (crew.car_id != null && carRepo.getById(crew.car_id) == null) {
            issues += ValidationIssue("car_id", "Car does not exist")
        }
        if (crew.team_id != null && teamRepo.getById(crew.team_id) == null) {
            issues += ValidationIssue("team_id", "Team does not exist")
        }

        return issues
    }

    val getAllRoute = get("/crews") {
        logger.info("Received GET /crews")
        val crews = repo.getAll()
        call.respond(crews)
        logger.info("Responded GET /crews with ${crews.size} items")
    }

    val getByIdRoute = get("/crews/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@get
        logger.info("Received GET /crews/$id")
        val crew = repo.getById(id)
        if (crew == null) {
            logger.warn("GET /crews/$id returned 404 - crew not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded GET /crews/$id with crew: ${crew.crew_id}")
            call.respond(crew)
        }
    }

    val createRoute = post("/crews") {
        val crew = call.receive<Crew>()
        logger.info("Received POST /crews with driverID: ${crew.driver_id}, codriverId: ${crew.codriver_id}")

        val issues = validateResources(crew)
        if (issues.isNotEmpty()) {
            logger.warn("POST /crews rejected - resource validation failed with ${issues.size} issue(s)")
            call.respond(
                HttpStatusCode.BadRequest,
                ValidationErrorResponse(details = issues)
            )
            return@post
        }

        val newId = repo.create(crew)

        call.response.headers.append(Location, "/crews/$newId")
        call.respond(HttpStatusCode.Created, mapOf("crew_id" to newId))
        logger.info("Responded POST /crews with 201 Created, new crew_id: $newId")
    }

    val updateRoute = put("/crews/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@put
        val crew = call.receive<Crew>()
        logger.info("Received PUT /crews/$id with crew: ${crew.crew_id}, driver_id: ${crew.driver_id}")

        val issues = validateResources(crew)
        if (issues.isNotEmpty()) {
            logger.warn("PUT /crews/$id rejected - resource validation failed with ${issues.size} issue(s)")
            call.respond(
                HttpStatusCode.BadRequest,
                ValidationErrorResponse(details = issues)
            )
            return@put
        }

        val rows = repo.update(id, crew)

        if (rows == 0) {
            logger.warn("PUT /crews/$id returned 404 - crew not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded PUT /crews/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    val deleteRoute = delete("/crews/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@delete
        logger.info("Received DELETE /crews/$id")
        val rows = repo.delete(id)

        if (rows == 0) {
            logger.warn("DELETE /crews/$id returned 404 - crew not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded DELETE /crews/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    attachCrewOpenApi(
        getAll = getAllRoute,
        getById = getByIdRoute,
        create = createRoute,
        update = updateRoute,
        delete = deleteRoute
    )
}