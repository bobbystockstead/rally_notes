package com.racing.routes.docs

import com.racing.data.RallyStageMap
import com.racing.data.ValidationErrorResponse
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun attachRallyStageMapOpenApi(
    getAll: Route,
    getById: Route,
    create: Route,
    update: Route,
    delete: Route,
) {
    getAll.describe {
        tag("RallyStageMaps")
        summary = "Get all RallyStageMaps"
        responses {
            HttpStatusCode.OK {
                description = "List of RallyStageMap records"
                schema = jsonSchema<List<RallyStageMap>>()
            }
        }
    }

    getById.describe {
        tag("RallyStageMaps")
        summary = "Get RallyStageMap by id"
        responses {
            HttpStatusCode.OK {
                description = "RallyStageMap found"
                schema = jsonSchema<RallyStageMap>()
            }
            HttpStatusCode.NotFound {
                description = "RallyStageMap not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    create.describe {
        tag("RallyStageMaps")
        summary = "Create rallyStageMap"
        requestBody {
            required = true
            schema = jsonSchema<RallyStageMap>()
        }
        responses {
            HttpStatusCode.Created {
                description = "RallyStageMap created"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid request: missing required rally_id/stage_id and/or one or more referenced IDs do not exist (rally, stage)"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    update.describe {
        tag("RallyStageMaps")
        summary = "Update rallyStageMap"
        requestBody {
            required = true
            schema = jsonSchema<RallyStageMap>()
        }
        responses {
            HttpStatusCode.NoContent {
                description = "RallyStageMap updated"
            }
            HttpStatusCode.NotFound {
                description = "RallyStageMap not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid request: missing required rally_id/stage_id and/or one or more referenced IDs do not exist (rally, stage)"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    delete.describe {
        tag("RallyStageMaps")
        summary = "Delete rallyStageMap"
        responses {
            HttpStatusCode.NoContent {
                description = "RallyStageMap deleted"
            }
            HttpStatusCode.NotFound {
                description = "RallyStageMap not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }
}

