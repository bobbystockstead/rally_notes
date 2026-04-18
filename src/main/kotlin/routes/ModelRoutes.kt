package com.racing.routes

import com.racing.data.Model
import com.racing.data.ValidationErrorResponse
import com.racing.data.ValidationIssue
import com.racing.db.ManufacturerRepository
import com.racing.db.ModelRepository
import com.racing.routes.docs.attachModelOpenApi
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Location
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("ModelRoutes")

fun Route.modelRoutes(repo: ModelRepository, manufacturerRepo: ManufacturerRepository) {

    fun manufacturerExists(id: Int): Boolean {
        return manufacturerRepo.getById(id) != null
    }
    val getAllRoute = get("/models") {
        logger.info("Received GET /models")
        val models = repo.getAll()
        call.respond(models)
        logger.info("Responded GET /models with ${models.size} items")
    }

    val getByIdRoute = get("/models/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@get
        logger.info("Received GET /models/$id")
        val model = repo.getById(id)
        if (model == null) {
            logger.warn("GET /models/$id returned 404 - model not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded GET /models/$id with model: ${model.name}")
            call.respond(model)
        }
    }

    val createRoute = post("/models") {
        val model = call.receive<Model>()
        logger.info("Received POST /models with model: ${model.name}, manufacturer_id: ${model.manufacturer_id}")

        // Validate manufacturer exists before attempting create
        if (model.manufacturer_id != null && !manufacturerExists(model.manufacturer_id)) {
            logger.warn("POST /models rejected - manufacturer_id ${model.manufacturer_id} does not exist")
            call.respond(
                HttpStatusCode.BadRequest,
                ValidationErrorResponse(
                    details = listOf(ValidationIssue("manufacturer_id", "Manufacturer does not exist"))
                )
            )
            return@post
        }

        val newId = repo.create(model)

        call.response.headers.append(Location, "/models/$newId")
        call.respond(HttpStatusCode.Created, mapOf("model_id" to newId))
        logger.info("Responded POST /models with 201 Created, new model_id: $newId")
    }

    val updateRoute = put("/models/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@put
        val model = call.receive<Model>()
        logger.info("Received PUT /models/$id with model: ${model.name}, manufacturer_id: ${model.manufacturer_id}")

        // Validate manufacturer exists before attempting update
        if (model.manufacturer_id != null && !manufacturerExists(model.manufacturer_id)) {
            logger.warn("PUT /models/$id rejected - manufacturer_id ${model.manufacturer_id} does not exist")
            call.respond(
                HttpStatusCode.BadRequest,
                ValidationErrorResponse(
                    details = listOf(ValidationIssue("manufacturer_id", "Manufacturer does not exist"))
                )
            )
            return@put
        }

        val rows = repo.update(id, model)

        if (rows == 0) {
            logger.warn("PUT /models/$id returned 404 - model not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded PUT /models/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    val deleteRoute = delete("/models/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@delete
        logger.info("Received DELETE /models/$id")
        val rows = repo.delete(id)

        if (rows == 0) {
            logger.warn("DELETE /models/$id returned 404 - model not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded DELETE /models/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    attachModelOpenApi(
        getAll = getAllRoute,
        getById = getByIdRoute,
        create = createRoute,
        update = updateRoute,
        delete = deleteRoute
    )
}