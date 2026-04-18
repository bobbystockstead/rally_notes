package com.racing.routes

import com.racing.data.RallyEntry
import com.racing.data.ValidationErrorResponse
import com.racing.data.ValidationIssue
import com.racing.db.*
import com.racing.routes.docs.attachRallyEntryOpenApi
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Location
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("RallyEntryRoutes")

fun Route.rallyEntryRoutes(repo: RallyEntryRepository, rallyRepo: RallyRepository, crewRepo: CrewRepository) {

    fun validateResources(rallyEntry: RallyEntry): List<ValidationIssue> {
        val issues = mutableListOf<ValidationIssue>()

        if (rallyEntry.rally_id == null) {
            issues += ValidationIssue("rally_id", "Rally is required")
        } else if (rallyRepo.getById(rallyEntry.rally_id) == null) {
            issues += ValidationIssue("rally_id", "Rally does not exist")
        }
        if (rallyEntry.crew_id == null) {
            issues += ValidationIssue("crew_id", "Crew is required")
        } else if (crewRepo.getById(rallyEntry.crew_id) == null) {
            issues += ValidationIssue("crew_id", "Crew does not exist")
        }

        return issues
    }

    val getAllRoute = get("/rallyEntries") {
        logger.info("Received GET /rallyEntries")
        val crewRallyEntries = repo.getAll()
        call.respond(crewRallyEntries)
        logger.info("Responded GET /rallyEntries with ${crewRallyEntries.size} items")
    }

    val getByIdRoute = get("/rallyEntries/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@get
        logger.info("Received GET /rallyEntries/$id")
        val rallyEntry = repo.getById(id)
        if (rallyEntry == null) {
            logger.warn("GET /rallyEntries/$id returned 404 - rallyEntry not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded GET /rallyEntries/$id with rallyEntry: ${rallyEntry.rally_id}")
            call.respond(rallyEntry)
        }
    }

    val createRoute = post("/rallyEntries") {
        val rallyEntry = call.receive<RallyEntry>()
        logger.info("Received POST /rallyEntries with rally_id: ${rallyEntry.rally_id}, crew_id: ${rallyEntry.crew_id}")

        val issues = validateResources(rallyEntry)
        if (issues.isNotEmpty()) {
            logger.warn("POST /rallyEntries rejected - resource validation failed with ${issues.size} issue(s)")
            call.respond(
                HttpStatusCode.BadRequest,
                ValidationErrorResponse(details = issues)
            )
            return@post
        }

        val newId = repo.create(rallyEntry)

        call.response.headers.append(Location, "/rallyEntries/$newId")
        call.respond(HttpStatusCode.Created, mapOf("entry_id" to newId))
        logger.info("Responded POST /rallyEntries with 201 Created, new entry_id: $newId")
    }

    val updateRoute = put("/rallyEntries/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@put
        val rallyEntry = call.receive<RallyEntry>()
        logger.info("Received PUT /rallyEntries/$id with rallyEntry: ${rallyEntry.entry_id}, rally_id: ${rallyEntry.rally_id}")

        val issues = validateResources(rallyEntry)
        if (issues.isNotEmpty()) {
            logger.warn("PUT /rallyEntries/$id rejected - resource validation failed with ${issues.size} issue(s)")
            call.respond(
                HttpStatusCode.BadRequest,
                ValidationErrorResponse(details = issues)
            )
            return@put
        }

        val rows = repo.update(id, rallyEntry)

        if (rows == 0) {
            logger.warn("PUT /rallyEntries/$id returned 404 - rallyEntry not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded PUT /rallyEntries/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    val deleteRoute = delete("/rallyEntries/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@delete
        logger.info("Received DELETE /rallyEntries/$id")
        val rows = repo.delete(id)

        if (rows == 0) {
            logger.warn("DELETE /rallyEntries/$id returned 404 - rallyEntry not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded DELETE /rallyEntries/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    attachRallyEntryOpenApi(
        getAll = getAllRoute,
        getById = getByIdRoute,
        create = createRoute,
        update = updateRoute,
        delete = deleteRoute
    )
}