package com.racing.routes

import com.racing.data.Codriver
import com.racing.db.CodriverRepository
import com.racing.routes.docs.attachCodriverOpenApi
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

private val logger = LoggerFactory.getLogger("CodriverRoutes")

fun Route.codriverRoutes(repo: CodriverRepository) {
    val getAllRoute = get("/codrivers") {
        logger.info("Received GET /codrivers")
        val codrivers = repo.getAll()
        call.respond(codrivers)
        logger.info("Responded GET /codrivers with ${codrivers.size} items")
    }

    val getByIdRoute = get("/codrivers/{id}") {
        val id = call.parameters["id"]!!.toInt()
        logger.info("Received GET /codrivers/$id")
        val codriver = repo.getById(id)
        if (codriver == null) {
            logger.warn("GET /codrivers/$id returned 404 - codriver not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded GET /codrivers/$id with codriver: ${codriver.name}")
            call.respond(codriver)
        }
    }

    val createRoute = post("/codrivers") {
        val codriver = call.receive<Codriver>()
        logger.info("Received POST /codrivers with codriver: ${codriver.name}, number: ${codriver.number}")
        val newId = repo.create(codriver)

        call.response.headers.append(Location, "/codrivers/$newId")
        call.respond(HttpStatusCode.Created, mapOf("codriver_id" to newId))
        logger.info("Responded POST /codrivers with 201 Created, new codriver_id: $newId")
    }

    val updateRoute = put("/codrivers/{id}") {
        val id = call.parameters["id"]!!.toInt()
        val codriver = call.receive<Codriver>()
        logger.info("Received PUT /codrivers/$id with codriver: ${codriver.name}, number: ${codriver.number}")
        val rows = repo.update(id, codriver)

        if (rows == 0) {
            logger.warn("PUT /codrivers/$id returned 404 - codriver not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded PUT /codrivers/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    val deleteRoute = delete("/codrivers/{id}") {
        val id = call.parameters["id"]!!.toInt()
        logger.info("Received DELETE /codrivers/$id")
        val rows = repo.delete(id)

        if (rows == 0) {
            logger.warn("DELETE /codrivers/$id returned 404 - codriver not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded DELETE /codrivers/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    attachCodriverOpenApi(
        getAll = getAllRoute,
        getById = getByIdRoute,
        create = createRoute,
        update = updateRoute,
        delete = deleteRoute
    )
}