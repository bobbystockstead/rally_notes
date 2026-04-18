package com.racing.routes

import com.racing.data.Team
import com.racing.db.ManufacturerRepository
import com.racing.db.TeamRepository
import com.racing.routes.docs.attachTeamOpenApi
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Location
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("TeamRoutes")

fun Route.teamRoutes(repo: TeamRepository, manufacturerRepo: ManufacturerRepository) {

    fun manufacturerExists(id: Int): Boolean {
        return manufacturerRepo.getById(id) != null
    }
    val getAllRoute = get("/teams") {
        logger.info("Received GET /teams")
        val teams = repo.getAll()
        call.respond(teams)
        logger.info("Responded GET /teams with ${teams.size} items")
    }

    val getByIdRoute = get("/teams/{id}") {
        val id = call.parameters["id"]!!.toInt()
        logger.info("Received GET /teams/$id")
        val team = repo.getById(id)
        if (team == null) {
            logger.warn("GET /teams/$id returned 404 - team not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded GET /teams/$id with team: ${team.name}")
            call.respond(team)
        }
    }

    val createRoute = post("/teams") {
        val team = call.receive<Team>()
        logger.info("Received POST /teams with team: ${team.name}, manufacturer_id: ${team.manufacturer_id}")

        // Validate manufacturer exists before attempting create
        if (team.manufacturer_id != null && !manufacturerExists(team.manufacturer_id)) {
            logger.warn("POST /teams rejected - manufacturer_id ${team.manufacturer_id} does not exist")
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Invalid manufacturer_id", "details" to "Manufacturer does not exist")
            )
            return@post
        }

        val newId = repo.create(team)

        call.response.headers.append(Location, "/teams/$newId")
        call.respond(HttpStatusCode.Created, mapOf("team_id" to newId))
        logger.info("Responded POST /teams with 201 Created, new team_id: $newId")
    }

    val updateRoute = put("/teams/{id}") {
        val id = call.parameters["id"]!!.toInt()
        val team = call.receive<Team>()
        logger.info("Received PUT /teams/$id with team: ${team.name}, manufacturer_id: ${team.manufacturer_id}")

        // Validate manufacturer exists before attempting update
        if (team.manufacturer_id != null && !manufacturerExists(team.manufacturer_id)) {
            logger.warn("PUT /teams/$id rejected - manufacturer_id ${team.manufacturer_id} does not exist")
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Invalid manufacturer_id", "details" to "Manufacturer does not exist")
            )
            return@put
        }

        val rows = repo.update(id, team)

        if (rows == 0) {
            logger.warn("PUT /teams/$id returned 404 - team not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded PUT /teams/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    val deleteRoute = delete("/teams/{id}") {
        val id = call.parameters["id"]!!.toInt()
        logger.info("Received DELETE /teams/$id")
        val rows = repo.delete(id)

        if (rows == 0) {
            logger.warn("DELETE /teams/$id returned 404 - team not found")
            call.respond(HttpStatusCode.NotFound)
        } else {
            logger.info("Responded DELETE /teams/$id with 204 No Content")
            call.respond(HttpStatusCode.NoContent)
        }
    }

    attachTeamOpenApi(
        getAll = getAllRoute,
        getById = getByIdRoute,
        create = createRoute,
        update = updateRoute,
        delete = deleteRoute
    )
}