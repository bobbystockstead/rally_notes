package com.racing.routes

import com.racing.data.NoteSet
import com.racing.data.ValidationErrorResponse
import com.racing.data.ValidationIssue
import com.racing.db.*
import com.racing.routes.docs.attachNoteSetOpenApi
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Location
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("NoteSetRoutes")

fun Route.noteSetRoutes(repo: NoteSetRepository, crewRepo: CrewRepository, stageRepo: StageRepository) {

    fun validateResources(noteSet: NoteSet): List<ValidationIssue> {
        val issues = mutableListOf<ValidationIssue>()

        if (noteSet.crew_id == null) {
            issues += ValidationIssue("crew_id", "Crew is required")
        } else if (crewRepo.getById(noteSet.crew_id) == null) {
            issues += ValidationIssue("crew_id", "Crew does not exist")
        }
        if (noteSet.stage_id == null) {
            issues += ValidationIssue("stage_id", "Stage is required")
        } else if (stageRepo.getById(noteSet.stage_id) == null) {
            issues += ValidationIssue("stage_id", "Stage does not exist")
        }

        return issues
    }

    val getAllRoute = get("/noteSets") {
        logger.info("Received GET /noteSets")
        val stageNoteSets = repo.getAll()
        call.respond(stageNoteSets)
        logger.info("Responded GET /noteSets with ${stageNoteSets.size} items")
    }

    val getByIdRoute = get("/noteSets/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@get
        logger.info("Received GET /noteSets/$id")
        val noteSet = repo.getById(id)
        if (noteSet == null) {
            logger.warn("GET /noteSets/$id returned 404 - noteSet not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded GET /noteSets/$id with noteSet: ${noteSet.note_id}")
            call.respond(noteSet)
        }
    }

    val createRoute = post("/noteSets") {
        val noteSet = call.receive<NoteSet>()
        logger.info("Received POST /noteSets with crew_id: ${noteSet.crew_id}, stage_id: ${noteSet.stage_id}")

        val issues = validateResources(noteSet)
        if (issues.isNotEmpty()) {
            logger.warn("POST /noteSets rejected - resource validation failed with ${issues.size} issue(s)")
            call.respond(
                HttpStatusCode.BadRequest,
                ValidationErrorResponse(details = issues)
            )
            return@post
        }

        val newId = repo.create(noteSet)

        call.response.headers.append(Location, "/noteSets/$newId")
        call.respond(HttpStatusCode.Created, mapOf("note_id" to newId))
        logger.info("Responded POST /noteSets with 201 Created, new note_id: $newId")
    }

    val updateRoute = put("/noteSets/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@put
        val noteSet = call.receive<NoteSet>()
        logger.info("Received PUT /noteSets/$id with noteSet: ${noteSet.note_id}, crew_id: ${noteSet.crew_id}")

        val issues = validateResources(noteSet)
        if (issues.isNotEmpty()) {
            logger.warn("PUT /noteSets/$id rejected - resource validation failed with ${issues.size} issue(s)")
            call.respond(
                HttpStatusCode.BadRequest,
                ValidationErrorResponse(details = issues)
            )
            return@put
        }

        val rows = repo.update(id, noteSet)

        if (rows == 0) {
            logger.warn("PUT /noteSets/$id returned 404 - noteSet not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded PUT /noteSets/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    val deleteRoute = delete("/noteSets/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@delete
        logger.info("Received DELETE /noteSets/$id")
        val rows = repo.delete(id)

        if (rows == 0) {
            logger.warn("DELETE /noteSets/$id returned 404 - noteSet not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded DELETE /noteSets/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    attachNoteSetOpenApi(
        getAll = getAllRoute,
        getById = getByIdRoute,
        create = createRoute,
        update = updateRoute,
        delete = deleteRoute
    )
}