package com.racing.routes

import com.racing.data.Manufacturer
import com.racing.db.ManufacturerRepository
import com.racing.routes.docs.attachManufacturerOpenApi
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Location
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("ManufacturerRoutes")

fun Route.manufacturerRoutes(repo: ManufacturerRepository) {
    val getAllRoute = get("/manufacturers") {
        logger.info("Received GET /manufacturers")
        val manufacturers = repo.getAll()
        call.respond(manufacturers)
        logger.info("Responded GET /manufacturers with ${manufacturers.size} items")
    }

    val getByIdRoute = get("/manufacturers/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@get
        logger.info("Received GET /manufacturers/$id")
        val manufacturer = repo.getById(id)
        if (manufacturer == null) {
            logger.warn("GET /manufacturers/$id returned 404 - manufacturer not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded GET /manufacturers/$id with manufacturer: ${manufacturer.name}")
            call.respond(manufacturer)
        }
    }

    val createRoute = post("/manufacturers") {
        val manufacturer = call.receive<Manufacturer>()
        logger.info("Received POST /manufacturers with manufacturer: ${manufacturer.name}")
        val newId = repo.create(manufacturer)

        call.response.headers.append(Location, "/manufacturers/$newId")
        call.respond(HttpStatusCode.Created, mapOf("manufacturer_id" to newId))
        logger.info("Responded POST /manufacturers with 201 Created, new manufacturer_id: $newId")
    }

    val updateRoute = put("/manufacturers/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@put
        val manufacturer = call.receive<Manufacturer>()
        logger.info("Received PUT /manufacturers/$id with manufacturer: ${manufacturer.name}")
        val rows = repo.update(id, manufacturer)

        if (rows == 0) {
            logger.warn("PUT /manufacturers/$id returned 404 - manufacturer not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded PUT /manufacturers/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    val deleteRoute = delete("/manufacturers/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@delete
        logger.info("Received DELETE /manufacturers/$id")
        val rows = repo.delete(id)

        if (rows == 0) {
            logger.warn("DELETE /manufacturers/$id returned 404 - manufacturer not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded DELETE /manufacturers/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    attachManufacturerOpenApi(
        getAll = getAllRoute,
        getById = getByIdRoute,
        create = createRoute,
        update = updateRoute,
        delete = deleteRoute
    )
}