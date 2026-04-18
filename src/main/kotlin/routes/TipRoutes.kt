package com.racing.routes

import com.racing.data.Tip
import com.racing.db.TipRepository
import com.racing.routes.docs.attachTipOpenApi
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Location
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("TipRoutes")

fun Route.tipRoutes(repo: TipRepository) {
    val getAllRoute = get("/tips") {
        logger.info("Received GET /tips")
        val tips = repo.getAll()
        call.respond(tips)
        logger.info("Responded GET /tips with ${tips.size} items")
    }

    val getByIdRoute = get("/tips/{id}") {
        val id = call.parameters["id"]!!.toInt()
        logger.info("Received GET /tips/$id")
        val tip = repo.getById(id)
        if (tip == null) {
            logger.warn("GET /tips/$id returned 404 - tip not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded GET /tips/$id with tip: ${tip.description}")
            call.respond(tip)
        }
    }

    val createRoute = post("/tips") {
        val tip = call.receive<Tip>()
        logger.info("Received POST /tips with tip: ${tip.description}")
        val newId = repo.create(tip)

        call.response.headers.append(Location, "/tips/$newId")
        call.respond(HttpStatusCode.Created, mapOf("tip_id" to newId))
        logger.info("Responded POST /tips with 201 Created, new tip_id: $newId")
    }

    val updateRoute = put("/tips/{id}") {
        val id = call.parameters["id"]!!.toInt()
        val tip = call.receive<Tip>()
        logger.info("Received PUT /tips/$id with tip: ${tip.description}")
        val rows = repo.update(id, tip)

        if (rows == 0) {
            logger.warn("PUT /tips/$id returned 404 - tip not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded PUT /tips/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    val deleteRoute = delete("/tips/{id}") {
        val id = call.parameters["id"]!!.toInt()
        logger.info("Received DELETE /tips/$id")
        val rows = repo.delete(id)

        if (rows == 0) {
            logger.warn("DELETE /tips/$id returned 404 - tip not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded DELETE /tips/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    attachTipOpenApi(
        getAll = getAllRoute,
        getById = getByIdRoute,
        create = createRoute,
        update = updateRoute,
        delete = deleteRoute
    )
}