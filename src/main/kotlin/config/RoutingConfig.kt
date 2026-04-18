package com.racing.config

import com.racing.data.RallyStageMap
import com.racing.db.*
import com.racing.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    val callRepo = CallRepository()
    val carRepo = CarRepository()
    val crewRepo = CrewRepository()
    val codriverRepo = CodriverRepository()
    val driverRepo = DriverRepository()
    val intensityRepo = IntensityRepository()
    val manufacturerRepo = ManufacturerRepository()
    val modelRepo = ModelRepository()
    val noteSetRepo = NoteSetRepository()
    val rallyRepo = RallyRepository()
    val rallyEntryRepo = RallyEntryRepository()
    val rallyStageMapRepo = RallyStageMapRepository()
    val stageRepo = StageRepository()
    val teamRepo = TeamRepository()
    val tipRepo = TipRepository()
    val warningRepo = WarningRepository()

    routing {
        route("/") {
            callRoutes(callRepo, noteSetRepo, intensityRepo, warningRepo, tipRepo)
            carRoutes(carRepo, modelRepo)
            crewRoutes(crewRepo, driverRepo, codriverRepo, carRepo, teamRepo)
            codriverRoutes(codriverRepo)
            driverRoutes(driverRepo)
            intensityRoutes(intensityRepo)
            manufacturerRoutes(manufacturerRepo)
            modelRoutes(modelRepo, manufacturerRepo)
            noteSetRoutes(noteSetRepo, crewRepo, stageRepo)
            stageRoutes(stageRepo)
            rallyRoutes(rallyRepo)
            rallyEntryRoutes(rallyEntryRepo, rallyRepo, crewRepo)
            rallyStageMapRoutes(rallyStageMapRepo, rallyRepo, stageRepo)
            teamRoutes(teamRepo, manufacturerRepo)
            tipRoutes(tipRepo)
            warningRoutes(warningRepo)
        }
    }
}
