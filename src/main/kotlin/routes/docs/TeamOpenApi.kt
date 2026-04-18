package com.racing.routes.docs

import com.racing.data.Team
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun attachTeamOpenApi(
    getAll: Route,
    getById: Route,
    create: Route,
    update: Route,
    delete: Route,
) {
    getAll.describe {
        tag("Teams")
        summary = "Get all teams"
        responses {
            HttpStatusCode.OK {
                description = "List of team records"
                schema = jsonSchema<List<Team>>()
            }
        }
    }

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

    create.describe {
        tag("Teams")
        summary = "Create team"
        requestBody {
            required = true
            schema = jsonSchema<Team>()
        }
        responses {
            HttpStatusCode.Created {
                description = "Team created"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid manufacturer_id - manufacturer does not exist"
            }
        }
    }

    update.describe {
        tag("Teams")
        summary = "Update team"
        requestBody {
            required = true
            schema = jsonSchema<Team>()
        }
        responses {
            HttpStatusCode.NoContent {
                description = "Team updated"
            }
            HttpStatusCode.NotFound {
                description = "Team not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid manufacturer_id - manufacturer does not exist"
            }
        }
    }

    delete.describe {
        tag("Teams")
        summary = "Delete team"
        responses {
            HttpStatusCode.NoContent {
                description = "Team deleted"
            }
            HttpStatusCode.NotFound {
                description = "Team not found"
            }
        }
    }
}

