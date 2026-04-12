package com.racing.config

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.routing.openapi.hide
import io.ktor.utils.io.ExperimentalKtorApi

@OptIn(ExperimentalKtorApi::class)
fun Application.configureHealth() {
    routing {
        get("/health") {
            call.respond(HttpStatusCode.OK, mapOf("status" to "UP"))
        }.hide()
    }
}

