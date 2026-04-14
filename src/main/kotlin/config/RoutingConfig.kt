package com.racing.config

import com.racing.db.*
import com.racing.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val rallyRepo = RallyRepository()
    val driverRepo = DriverRepository()
    val codriverRepo = CodriverRepository()

    routing {
        route("/") {
            rallyRoutes(rallyRepo)
            driverRoutes(driverRepo)
            codriverRoutes(codriverRepo)
        }
    }
}
