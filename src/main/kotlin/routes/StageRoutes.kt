package com.racing.routes

import com.racing.data.Stage
import com.racing.db.StageRepository
import com.racing.routes.docs.attachStageOpenApi
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Location
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("StageRoutes")

fun Route.stageRoutes(repo: StageRepository) {
    val getAllRoute = get("/stages") {
        logger.info("Received GET /stages")
        val stages = repo.getAll()
        call.respond(stages)
        logger.info("Responded GET /stages with ${stages.size} items")
    }

    val getByIdRoute = get("/stages/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@get
        logger.info("Received GET /stages/$id")
        val stage = repo.getById(id)
        if (stage == null) {
            logger.warn("GET /stages/$id returned 404 - stage not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded GET /stages/$id with stage: ${stage.name}")
            call.respond(stage)
        }
    }

    val createRoute = post("/stages") {
        val stage = call.receive<Stage>()
        logger.info("Received POST /stages with stage: ${stage.name}, distance: ${stage.distance}")
        val newId = repo.create(stage)

        call.response.headers.append(Location, "/stages/$newId")
        call.respond(HttpStatusCode.Created, mapOf("stage_id" to newId))
        logger.info("Responded POST /stages with 201 Created, new stage_id: $newId")
    }

    val updateRoute = put("/stages/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@put
        val stage = call.receive<Stage>()
        logger.info("Received PUT /stages/$id with stage: ${stage.name}, distance: ${stage.distance}")
        val rows = repo.update(id, stage)

        if (rows == 0) {
            logger.warn("PUT /stages/$id returned 404 - stage not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded PUT /stages/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    val deleteRoute = delete("/stages/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@delete
        logger.info("Received DELETE /stages/$id")
        val rows = repo.delete(id)

        if (rows == 0) {
            logger.warn("DELETE /stages/$id returned 404 - stage not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded DELETE /stages/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    attachStageOpenApi(
        getAll = getAllRoute,
        getById = getByIdRoute,
        create = createRoute,
        update = updateRoute,
        delete = deleteRoute
    )
}