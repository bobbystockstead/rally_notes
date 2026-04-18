package com.racing.routes

import com.racing.data.Car
import com.racing.data.ValidationErrorResponse
import com.racing.data.ValidationIssue
import com.racing.db.ModelRepository
import com.racing.db.CarRepository
import com.racing.routes.docs.attachCarOpenApi
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Location
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("CarRoutes")

fun Route.carRoutes(repo: CarRepository, modelRepo: ModelRepository) {

    fun modelExists(id: Int): Boolean {
        return modelRepo.getById(id) != null
    }
    val getAllRoute = get("/cars") {
        logger.info("Received GET /cars")
        val cars = repo.getAll()
        call.respond(cars)
        logger.info("Responded GET /cars with ${cars.size} items")
    }

    val getByIdRoute = get("/cars/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@get
        logger.info("Received GET /cars/$id")
        val car = repo.getById(id)
        if (car == null) {
            logger.warn("GET /cars/$id returned 404 - car not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded GET /cars/$id with car: ${car.name}")
            call.respond(car)
        }
    }

    val createRoute = post("/cars") {
        val car = call.receive<Car>()
        logger.info("Received POST /cars with car: ${car.name}, model_id: ${car.model_id}")

        // Validate model exists before attempting create
        if (car.model_id != null && !modelExists(car.model_id)) {
            logger.warn("POST /cars rejected - model_id ${car.model_id} does not exist")
            call.respond(
                HttpStatusCode.BadRequest,
                ValidationErrorResponse(
                    details = listOf(ValidationIssue("model_id", "Model does not exist"))
                )
            )
            return@post
        }

        val newId = repo.create(car)

        call.response.headers.append(Location, "/cars/$newId")
        call.respond(HttpStatusCode.Created, mapOf("car_id" to newId))
        logger.info("Responded POST /cars with 201 Created, new car_id: $newId")
    }

    val updateRoute = put("/cars/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@put
        val car = call.receive<Car>()
        logger.info("Received PUT /cars/$id with car: ${car.name}, model_id: ${car.model_id}")

        // Validate model exists before attempting update
        if (car.model_id != null && !modelExists(car.model_id)) {
            logger.warn("PUT /cars/$id rejected - model_id ${car.model_id} does not exist")
            call.respond(
                HttpStatusCode.BadRequest,
                ValidationErrorResponse(
                    details = listOf(ValidationIssue("model_id", "Model does not exist"))
                )
            )
            return@put
        }

        val rows = repo.update(id, car)

        if (rows == 0) {
            logger.warn("PUT /cars/$id returned 404 - car not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded PUT /cars/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    val deleteRoute = delete("/cars/{id}") {
        val id = call.parsePathIdOrRespond(logger) ?: return@delete
        logger.info("Received DELETE /cars/$id")
        val rows = repo.delete(id)

        if (rows == 0) {
            logger.warn("DELETE /cars/$id returned 404 - car not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded DELETE /cars/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    attachCarOpenApi(
        getAll = getAllRoute,
        getById = getByIdRoute,
        create = createRoute,
        update = updateRoute,
        delete = deleteRoute
    )
}