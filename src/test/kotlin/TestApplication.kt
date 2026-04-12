package com.racing

import com.racing.config.configureErrorHandling
import com.racing.config.configureHealth
import com.racing.config.configureHTTP
import com.racing.config.configureOpenAPI
import com.racing.config.configureRouting
import com.racing.config.configureSerialization
import io.ktor.server.application.Application

fun Application.testModule() {
    // No DB init here
    configureErrorHandling()
    configureHealth()
    configureOpenAPI()
    configureHTTP()
    configureSerialization()
    configureRouting()
}

