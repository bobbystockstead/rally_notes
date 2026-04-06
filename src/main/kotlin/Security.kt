package com.racer

import io.ktor.server.application.*
import io.ktor.server.plugins.csrf.*

fun Application.configureSecurity() {
    install(CSRF) {
        // Allow requests from frontend running on different port
        allowOrigin("http://localhost:3000")
        allowOrigin("http://localhost:8080")
        allowOrigin("http://127.0.0.1:3000")
        allowOrigin("http://127.0.0.1:8080")

        // For development: disable strict origin matching since frontend and backend are on different ports
        // In production, you should enable originMatchesHost() and configure trusted origins
    }
}
