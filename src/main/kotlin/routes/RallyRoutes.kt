package com.racing.routes

import com.racing.data.Rally
import com.racing.db.RallyRepository
import com.racing.routes.docs.attachRallyOpenApi
import io.ktor.http.HttpHeaders.Location
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("RallyRoutes")

fun Route.rallyRoutes(repo: RallyRepository) {
    val getAllRoute = get("/rallies") {
        logger.info("Received GET /rallies")
        val rallies = repo.getAll()
        call.respond(rallies)
        logger.info("Responded GET /rallies with ${rallies.size} items")
    }

    val getByIdRoute = get("/rallies/{id}") {
        val id = call.parameters["id"]!!.toInt()
        logger.info("Received GET /rallies/$id")
        val rally = repo.getById(id)
        if (rally == null) {
            logger.warn("GET /rallies/$id returned 404 - rally not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded GET /rallies/$id with rally: ${rally.name}")
            call.respond(rally)
        }
    }

    val createRoute = post("/rallies") {
        val rally = call.receive<Rally>()
        logger.info("Received POST /rallies with rally: ${rally.name}, date: ${rally.date}")
        val newId = repo.create(rally)

        call.response.headers.append(Location, "/api/v1/rallies/$newId")
        call.respond(HttpStatusCode.Created, mapOf("rally_id" to newId))
        logger.info("Responded POST /rallies with 201 Created, new rally_id: $newId")
    }

    val updateRoute = put("/rallies/{id}") {
        val id = call.parameters["id"]!!.toInt()
        val rally = call.receive<Rally>()
        logger.info("Received PUT /rallies/$id with rally: ${rally.name}, date: ${rally.date}")
        val rows = repo.update(id, rally)

        if (rows == 0) {
            logger.warn("PUT /rallies/$id returned 404 - rally not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded PUT /rallies/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    val deleteRoute = delete("/rallies/{id}") {
        val id = call.parameters["id"]!!.toInt()
        logger.info("Received DELETE /rallies/$id")
        val rows = repo.delete(id)

        if (rows == 0) {
            logger.warn("DELETE /rallies/$id returned 404 - rally not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded DELETE /rallies/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    attachRallyOpenApi(
        getAll = getAllRoute,
        getById = getByIdRoute,
        create = createRoute,
        update = updateRoute,
        delete = deleteRoute
    )
}