package com.racing

import com.racing.config.*
import io.ktor.server.application.*

fun Application.testModule() {
    // No DB init here
    configureErrorHandling()
    configureHealth()
    configureOpenAPI()
    configureHTTP()
    configureSerialization()
    configureRouting()
}

