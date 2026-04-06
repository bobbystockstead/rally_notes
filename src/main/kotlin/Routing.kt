package com.racer

import com.racer.models.*
import com.racer.repositories.RallyRepository
import com.racer.repositories.RepositoryException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val rallyRepository = RallyRepository()

    install(StatusPages) {
        exception<RepositoryException.NotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ApiResponse<String>(success = false, error = cause.message)
            )
        }
        exception<RepositoryException.ValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse<String>(success = false, error = cause.message)
            )
        }
        exception<RepositoryException.DataAccessException> { call, cause ->
            this@configureRouting.log.error("Database error", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse<String>(success = false, error = "Database operation failed")
            )
        }
        exception<Throwable> { call, cause ->
            this@configureRouting.log.error("Unexpected error", cause)
            call.respondText(text = "500: ${cause.message}", status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        get("/") {
            call.respondText("Rally Notes API - Running on Ktor with MySQL")
        }

        get("/health") {
            call.respond(ApiResponse(success = true, data = "OK"))
        }

        // Rally endpoints
        route("/api/rallies") {
            get {
                try {
                    val rallies = rallyRepository.findAll()
                    call.respond(ApiResponse(success = true, data = rallies))
                } catch (e: Exception) {
                    this@configureRouting.log.error("Error fetching rallies", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<String>(success = false, error = "Failed to fetch rallies"))
                }
            }

            post {
                try {
                    val request = call.receive<CreateRallyRequest>()
                    val id = rallyRepository.create(request.name, request.date)
                    val rally = rallyRepository.findById(id)
                    call.respond(HttpStatusCode.Created, ApiResponse(success = true, data = rally))
                } catch (e: Exception) {
                    this@configureRouting.log.error("Error creating rally", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<String>(success = false, error = "Failed to create rally"))
                }
            }

            get("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid rally id")
                    val rally = rallyRepository.findById(id)
                    call.respond(ApiResponse(success = true, data = rally))
                } catch (e: RepositoryException.NotFoundException) {
                    call.respond(HttpStatusCode.NotFound, ApiResponse<String>(success = false, error = e.message))
                } catch (e: Exception) {
                    this@configureRouting.log.error("Error fetching rally", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<String>(success = false, error = "Failed to fetch rally"))
                }
            }

            put("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid rally id")
                    val request = call.receive<UpdateRallyRequest>()

                    val existing = rallyRepository.findById(id)
                    val updated = rallyRepository.update(
                        id,
                        request.name ?: existing.name,
                        request.date ?: existing.date
                    )

                    if (updated) {
                        val rally = rallyRepository.findById(id)
                        call.respond(ApiResponse(success = true, data = rally))
                    } else {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<String>(success = false, error = "Rally not found"))
                    }
                } catch (e: RepositoryException.NotFoundException) {
                    call.respond(HttpStatusCode.NotFound, ApiResponse<String>(success = false, error = e.message))
                } catch (e: Exception) {
                    this@configureRouting.log.error("Error updating rally", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<String>(success = false, error = "Failed to update rally"))
                }
            }

            delete("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid rally id")
                    val deleted = rallyRepository.delete(id)

                    if (deleted) {
                        call.respond(ApiResponse(success = true, data = "Rally deleted successfully"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<String>(success = false, error = "Rally not found"))
                    }
                } catch (e: Exception) {
                    this@configureRouting.log.error("Error deleting rally", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<String>(success = false, error = "Failed to delete rally"))
                }
            }
        }

        // Health check
        route("/api/health") {
            get {
                call.respond(ApiResponse(success = true, data = mapOf("status" to "healthy")))
            }
        }
    }
}
