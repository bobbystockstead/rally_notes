package com.racing.routes

import com.racing.data.RallyStageMap
import com.racing.data.ValidationErrorResponse
import com.racing.data.ValidationIssue
import com.racing.db.*
import com.racing.routes.docs.attachRallyStageMapOpenApi
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Location
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("RallyStageMapRoutes")

fun Route.rallyStageMapRoutes(repo: RallyStageMapRepository, rallyRepo: RallyRepository, stageRepo: StageRepository) {

    fun validateResources(rallyStageMap: RallyStageMap): List<ValidationIssue> {
        val issues = mutableListOf<ValidationIssue>()

        if (rallyStageMap.rally_id == null) {
            issues += ValidationIssue("rally_id", "Rally is required")
        } else if (rallyRepo.getById(rallyStageMap.rally_id) == null) {
            issues += ValidationIssue("rally_id", "Rally does not exist")
        }
        if (rallyStageMap.stage_id == null) {
            issues += ValidationIssue("stage_id", "Stage is required")
        } else if (stageRepo.getById(rallyStageMap.stage_id) == null) {
            issues += ValidationIssue("stage_id", "Stage does not exist")
        }

        return issues
    }

    val getAllRoute = get("/rallyStageMaps") {
        logger.info("Received GET /rallyStageMaps")
        val rallyStageMaps = repo.getAll()
        call.respond(rallyStageMaps)
        logger.info("Responded GET /rallyStageMaps with ${rallyStageMaps.size} items")
    }

    val getByIdRoute = get("/rallyStageMaps/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@get
        logger.info("Received GET /rallyStageMaps/$id")
        val rallyStageMap = repo.getById(id)
        if (rallyStageMap == null) {
            logger.warn("GET /rallyStageMaps/$id returned 404 - rallyStageMap not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded GET /rallyStageMaps/$id with rallyStageMap: ${rallyStageMap.rally_stage_id}")
            call.respond(rallyStageMap)
        }
    }

    val createRoute = post("/rallyStageMaps") {
        val rallyStageMap = call.receive<RallyStageMap>()
        logger.info("Received POST /rallyStageMaps with rally_id: ${rallyStageMap.rally_id}, stage_id: ${rallyStageMap.stage_id}")

        val issues = validateResources(rallyStageMap)
        if (issues.isNotEmpty()) {
            logger.warn("POST /rallyStageMaps rejected - resource validation failed with ${issues.size} issue(s)")
            call.respond(
                HttpStatusCode.BadRequest,
                ValidationErrorResponse(details = issues)
            )
            return@post
        }

        val newId = repo.create(rallyStageMap)

        call.response.headers.append(Location, "/rallyStageMaps/$newId")
        call.respond(HttpStatusCode.Created, mapOf("rally_stage_id" to newId))
        logger.info("Responded POST /rallyStageMaps with 201 Created, new rallyStageMap_id: $newId")
    }

    val updateRoute = put("/rallyStageMaps/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@put
        val rallyStageMap = call.receive<RallyStageMap>()
        logger.info("Received PUT /rallyStageMaps/$id with rallyStageMap: ${rallyStageMap.rally_stage_id}, rally_id: ${rallyStageMap.rally_id}")

        val issues = validateResources(rallyStageMap)
        if (issues.isNotEmpty()) {
            logger.warn("PUT /rallyStageMaps/$id rejected - resource validation failed with ${issues.size} issue(s)")
            call.respond(
                HttpStatusCode.BadRequest,
                ValidationErrorResponse(details = issues)
            )
            return@put
        }

        val rows = repo.update(id, rallyStageMap)

        if (rows == 0) {
            logger.warn("PUT /rallyStageMaps/$id returned 404 - rallyStageMap not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded PUT /rallyStageMaps/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    val deleteRoute = delete("/rallyStageMaps/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@delete
        logger.info("Received DELETE /rallyStageMaps/$id")
        val rows = repo.delete(id)

        if (rows == 0) {
            logger.warn("DELETE /rallyStageMaps/$id returned 404 - rallyStageMap not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded DELETE /rallyStageMaps/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    attachRallyStageMapOpenApi(
        getAll = getAllRoute,
        getById = getByIdRoute,
        create = createRoute,
        update = updateRoute,
        delete = deleteRoute
    )
}