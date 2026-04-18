package com.racing.routes

import com.racing.data.Intensity
import com.racing.db.IntensityRepository
import com.racing.routes.docs.attachIntensityOpenApi
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Location
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("IntensityRoutes")

fun Route.intensityRoutes(repo: IntensityRepository) {
    val getAllRoute = get("/intensities") {
        logger.info("Received GET /intensities")
        val intensities = repo.getAll()
        call.respond(intensities)
        logger.info("Responded GET /intensities with ${intensities.size} items")
    }

    val getByIdRoute = get("/intensities/{id}") {
        val id = call.parameters["id"]!!.toInt()
        logger.info("Received GET /intensities/$id")
        val intensity = repo.getById(id)
        if (intensity == null) {
            logger.warn("GET /intensities/$id returned 404 - intensity not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded GET /intensities/$id with intensity: ${intensity.name}")
            call.respond(intensity)
        }
    }

    val createRoute = post("/intensities") {
        val intensity = call.receive<Intensity>()
        logger.info("Received POST /intensities with intensity: ${intensity.name}")
        val newId = repo.create(intensity)

        call.response.headers.append(Location, "/intensities/$newId")
        call.respond(HttpStatusCode.Created, mapOf("intensity_id" to newId))
        logger.info("Responded POST /intensities with 201 Created, new intensity_id: $newId")
    }

    val updateRoute = put("/intensities/{id}") {
        val id = call.parameters["id"]!!.toInt()
        val intensity = call.receive<Intensity>()
        logger.info("Received PUT /intensities/$id with intensity: ${intensity.name}")
        val rows = repo.update(id, intensity)

        if (rows == 0) {
            logger.warn("PUT /intensities/$id returned 404 - intensity not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded PUT /intensities/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    val deleteRoute = delete("/intensities/{id}") {
        val id = call.parameters["id"]!!.toInt()
        logger.info("Received DELETE /intensities/$id")
        val rows = repo.delete(id)

        if (rows == 0) {
            logger.warn("DELETE /intensities/$id returned 404 - intensity not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded DELETE /intensities/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    attachIntensityOpenApi(
        getAll = getAllRoute,
        getById = getByIdRoute,
        create = createRoute,
        update = updateRoute,
        delete = deleteRoute
    )
}