package com.racing.routes

import com.racing.db.TeamRepository
import com.racing.routes.docs.attachTeamOpenApi
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.teamRoutes(repo: TeamRepository) {
    val getByIdRoute = get("/teams/{id}") {
        val id = call.parameters["id"]!!.toInt()
        val team = repo.getById(id)
        if (team == null) call.respond(HttpStatusCode.NotFound)
        else call.respond(team)
    }

    attachTeamOpenApi(getByIdRoute)
}