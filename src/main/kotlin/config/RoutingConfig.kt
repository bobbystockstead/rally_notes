package com.racing.config

import com.racing.db.RallyRepository
import com.racing.db.TeamRepository
import com.racing.routes.rallyRoutes
import com.racing.routes.teamRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val teamRepo = TeamRepository()
    val rallyRepo = RallyRepository()

    routing {
        route("/") {
            teamRoutes(teamRepo)
            rallyRoutes(rallyRepo)
        }
    }
}
