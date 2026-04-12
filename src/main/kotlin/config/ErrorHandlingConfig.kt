package com.racing.config

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import java.sql.SQLException

fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<SQLException> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Database operation failed", "details" to (cause.message ?: "Unknown error"))
            )
        }

        exception<NumberFormatException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Invalid ID format", "details" to (cause.message ?: "ID must be an integer"))
            )
        }

        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Unexpected server error", "details" to (cause.message ?: "Unknown error"))
            )
        }
    }
}


