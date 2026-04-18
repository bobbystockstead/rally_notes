package com.racing.config

import com.racing.db.*
import com.racing.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    val codriverRepo = CodriverRepository()
    val driverRepo = DriverRepository()
    val intensityRepo = IntensityRepository()
    val manufacturerRepo = ManufacturerRepository()
    val rallyRepo = RallyRepository()
    val tipRepo = TipRepository()
    val warningRepo = WarningRepository()

    routing {
        route("/") {
            codriverRoutes(codriverRepo)
            driverRoutes(driverRepo)
            intensityRoutes(intensityRepo)
            manufacturerRoutes(manufacturerRepo)
            rallyRoutes(rallyRepo)
            tipRoutes(tipRepo)
            warningRoutes(warningRepo)
        }
    }
}
