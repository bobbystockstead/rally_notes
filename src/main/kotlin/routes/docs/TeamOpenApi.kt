package com.racing.routes.docs

import com.racing.data.Team
import io.ktor.http.HttpStatusCode
import io.ktor.openapi.jsonSchema
import io.ktor.server.routing.Route
import io.ktor.server.routing.openapi.describe
import io.ktor.utils.io.ExperimentalKtorApi

@OptIn(ExperimentalKtorApi::class)
fun attachTeamOpenApi(getById: Route) {
    getById.describe {
        tag("Teams")
        summary = "Get team by id"
        responses {
            HttpStatusCode.OK {
                description = "Team found"
                schema = jsonSchema<Team>()
            }
            HttpStatusCode.NotFound {
                description = "Team not found"
            }
        }
    }
}

