package com.racing.config

import com.racing.db.*
import com.racing.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    val carRepo = CarRepository()
    val crewRepo = CrewRepository()
    val codriverRepo = CodriverRepository()
    val driverRepo = DriverRepository()
    val intensityRepo = IntensityRepository()
    val manufacturerRepo = ManufacturerRepository()
    val modelRepo = ModelRepository()
    val rallyRepo = RallyRepository()
    val teamRepo = TeamRepository()
    val tipRepo = TipRepository()
    val warningRepo = WarningRepository()

    routing {
        route("/") {
            carRoutes(carRepo, modelRepo)
            crewRoutes(crewRepo, driverRepo, codriverRepo, carRepo, teamRepo)
            codriverRoutes(codriverRepo)
            driverRoutes(driverRepo)
            intensityRoutes(intensityRepo)
            manufacturerRoutes(manufacturerRepo)
            modelRoutes(modelRepo, manufacturerRepo)
            rallyRoutes(rallyRepo)
            teamRoutes(teamRepo, manufacturerRepo)
            tipRoutes(tipRepo)
            warningRoutes(warningRepo)
        }
    }
}
