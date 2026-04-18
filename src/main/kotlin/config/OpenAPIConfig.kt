package com.racing.config

import io.ktor.openapi.*
import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureOpenAPI() {
    routing {

        swaggerUI(path = "swagger") {
            info = OpenApiInfo(
                title = "Rally Notes API",
                version = "1.0.0",
                description = "API for managing rally races and team information"
            )
        }
    }
}