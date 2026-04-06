package com.racer

import com.racer.database.configureDatabaseFactory
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    try {
        configureDatabaseFactory()
    } catch (e: Exception) {
        log.warn("Database initialization skipped or failed: ${e.message}")
    }
    configureHTTP()
    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureRouting()
}
