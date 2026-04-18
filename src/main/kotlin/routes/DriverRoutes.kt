package com.racing.routes

import com.racing.data.Driver
import com.racing.db.DriverRepository
import com.racing.routes.docs.attachDriverOpenApi
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Location
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("DriverRoutes")

fun Route.driverRoutes(repo: DriverRepository) {
    val getAllRoute = get("/drivers") {
        logger.info("Received GET /drivers")
        val drivers = repo.getAll()
        call.respond(drivers)
        logger.info("Responded GET /drivers with ${drivers.size} items")
    }

    val getByIdRoute = get("/drivers/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@get
        logger.info("Received GET /drivers/$id")
        val driver = repo.getById(id)
        if (driver == null) {
            logger.warn("GET /drivers/$id returned 404 - driver not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded GET /drivers/$id with driver: ${driver.name}")
            call.respond(driver)
        }
    }

    val createRoute = post("/drivers") {
        val driver = call.receive<Driver>()
        logger.info("Received POST /drivers with driver: ${driver.name}, number: ${driver.number}")
        val newId = repo.create(driver)

        call.response.headers.append(Location, "/drivers/$newId")
        call.respond(HttpStatusCode.Created, mapOf("driver_id" to newId))
        logger.info("Responded POST /drivers with 201 Created, new driver_id: $newId")
    }

    val updateRoute = put("/drivers/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@put
        val driver = call.receive<Driver>()
        logger.info("Received PUT /drivers/$id with driver: ${driver.name}, number: ${driver.number}")
        val rows = repo.update(id, driver)

        if (rows == 0) {
            logger.warn("PUT /drivers/$id returned 404 - driver not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded PUT /drivers/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    val deleteRoute = delete("/drivers/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@delete
        logger.info("Received DELETE /drivers/$id")
        val rows = repo.delete(id)

        if (rows == 0) {
            logger.warn("DELETE /drivers/$id returned 404 - driver not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded DELETE /drivers/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    attachDriverOpenApi(
        getAll = getAllRoute,
        getById = getByIdRoute,
        create = createRoute,
        update = updateRoute,
        delete = deleteRoute
    )
}