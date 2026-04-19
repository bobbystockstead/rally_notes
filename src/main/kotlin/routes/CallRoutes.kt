package com.racing.routes

import com.racing.data.Call
import com.racing.data.ValidationErrorResponse
import com.racing.data.ValidationIssue
import com.racing.db.*
import com.racing.routes.docs.attachCallOpenApi
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Location
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("CallRoutes")

fun Route.callRoutes(repo: CallRepository, noteSetRepo: NoteSetRepository, intensityRepo: IntensityRepository, warningRepo: WarningRepository, tipRepo: TipRepository) {

    fun validateResources(instruction: Call): List<ValidationIssue> {
        val issues = mutableListOf<ValidationIssue>()

        if (instruction.note_id == null) {
            issues += ValidationIssue("note_id", "Note is required")
        } else if (noteSetRepo.getById(instruction.note_id) == null) {
            issues += ValidationIssue("note_id", "Note does not exist")
        }

        if (instruction.sequence_number == null) {
            issues += ValidationIssue("sequence_number", "sequence_number is required")
        } 

        if (instruction.intensity_id != null && intensityRepo.getById(instruction.intensity_id) == null) {
            issues += ValidationIssue("intensity_id", "Intensity does not exist")
        }
        if (instruction.warning_id != null && warningRepo.getById(instruction.warning_id) == null) {
            issues += ValidationIssue("warning_id", "Warning does not exist")
        }
        if (instruction.tip_id != null && tipRepo.getById(instruction.tip_id) == null) {
            issues += ValidationIssue("tip_id", "Warning does not exist")
        }

        return issues
    }

    val getAllRoute = get("/calls") {
        logger.info("Received GET /calls")
        val calls = repo.getAll()
        call.respond(calls)
        logger.info("Responded GET /calls with ${calls.size} items")
    }

    val getByIdRoute = get("/calls/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@get
        logger.info("Received GET /calls/$id")
        val instruction = repo.getById(id)
        if (instruction == null) {
            logger.warn("GET /calls/$id returned 404 - call not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded GET /calls/$id with call: ${instruction.call_id}")
            call.respond(instruction)
        }
    }

    val createRoute = post("/calls") {
        val instruction = call.receive<Call>()
        logger.info("Received POST /calls with note_id: ${instruction.note_id}, sequence_number: ${instruction.sequence_number}")

        val issues = validateResources(instruction)
        if (issues.isNotEmpty()) {
            logger.warn("POST /calls rejected - resource validation failed with ${issues.size} issue(s)")
            call.respond(
                HttpStatusCode.BadRequest,
                ValidationErrorResponse(details = issues)
            )
            return@post
        }

        val newId = repo.create(instruction)

        call.response.headers.append(Location, "/calls/$newId")
        call.respond(HttpStatusCode.Created, mapOf("call_id" to newId))
        logger.info("Responded POST /calls with 201 Created, new call_id: $newId")
    }

    val updateRoute = put("/calls/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@put
        val instruction = call.receive<Call>()
        logger.info("Received PUT /calls/$id with call: ${instruction.call_id}, note_id: ${instruction.note_id}")

        val issues = validateResources(instruction)
        if (issues.isNotEmpty()) {
            logger.warn("PUT /calls/$id rejected - resource validation failed with ${issues.size} issue(s)")
            call.respond(
                HttpStatusCode.BadRequest,
                ValidationErrorResponse(details = issues)
            )
            return@put
        }

        val rows = repo.update(id, instruction)

        if (rows == 0) {
            logger.warn("PUT /calls/$id returned 404 - call not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded PUT /calls/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    val deleteRoute = delete("/calls/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@delete
        logger.info("Received DELETE /calls/$id")
        val rows = repo.delete(id)

        if (rows == 0) {
            logger.warn("DELETE /calls/$id returned 404 - call not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded DELETE /calls/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    attachCallOpenApi(
        getAll = getAllRoute,
        getById = getByIdRoute,
        create = createRoute,
        update = updateRoute,
        delete = deleteRoute
    )
}