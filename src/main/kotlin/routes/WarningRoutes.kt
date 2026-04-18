package com.racing.routes

import com.racing.data.Warning
import com.racing.db.WarningRepository
import com.racing.routes.docs.attachWarningOpenApi
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Location
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("WarningRoutes")

fun Route.warningRoutes(repo: WarningRepository) {
    val getAllRoute = get("/warnings") {
        logger.info("Received GET /warnings")
        val warnings = repo.getAll()
        call.respond(warnings)
        logger.info("Responded GET /warnings with ${warnings.size} items")
    }

    val getByIdRoute = get("/warnings/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@get
        logger.info("Received GET /warnings/$id")
        val warning = repo.getById(id)
        if (warning == null) {
            logger.warn("GET /warnings/$id returned 404 - warning not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded GET /warnings/$id with warning: ${warning.description}")
            call.respond(warning)
        }
    }

    val createRoute = post("/warnings") {
        val warning = call.receive<Warning>()
        logger.info("Received POST /warnings with warning: ${warning.description}")
        val newId = repo.create(warning)

        call.response.headers.append(Location, "/warnings/$newId")
        call.respond(HttpStatusCode.Created, mapOf("warning_id" to newId))
        logger.info("Responded POST /warnings with 201 Created, new warning_id: $newId")
    }

    val updateRoute = put("/warnings/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@put
        val warning = call.receive<Warning>()
        logger.info("Received PUT /warnings/$id with warning: ${warning.description}")
        val rows = repo.update(id, warning)

        if (rows == 0) {
            logger.warn("PUT /warnings/$id returned 404 - warning not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded PUT /warnings/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    val deleteRoute = delete("/warnings/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@delete
        logger.info("Received DELETE /warnings/$id")
        val rows = repo.delete(id)

        if (rows == 0) {
            logger.warn("DELETE /warnings/$id returned 404 - warning not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded DELETE /warnings/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    attachWarningOpenApi(
        getAll = getAllRoute,
        getById = getByIdRoute,
        create = createRoute,
        update = updateRoute,
        delete = deleteRoute
    )
}