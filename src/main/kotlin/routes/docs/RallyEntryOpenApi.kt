package com.racing.routes.docs

import com.racing.data.RallyEntry
import com.racing.data.ValidationErrorResponse
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun attachRallyEntryOpenApi(
    getAll: Route,
    getById: Route,
    create: Route,
    update: Route,
    delete: Route,
) {
    getAll.describe {
        tag("RallyEntries")
        summary = "Get all RallyEntries"
        responses {
            HttpStatusCode.OK {
                description = "List of RallyEntry records"
                schema = jsonSchema<List<RallyEntry>>()
            }
        }
    }

    getById.describe {
        tag("RallyEntries")
        summary = "Get RallyEntry by id"
        responses {
            HttpStatusCode.OK {
                description = "RallyEntry found"
                schema = jsonSchema<RallyEntry>()
            }
            HttpStatusCode.NotFound {
                description = "RallyEntry not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    create.describe {
        tag("RallyEntries")
        summary = "Create rallyEntry"
        requestBody {
            required = true
            schema = jsonSchema<RallyEntry>()
        }
        responses {
            HttpStatusCode.Created {
                description = "RallyEntry created"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid request: missing required rally_id/crew_id and/or one or more referenced IDs do not exist (rally, crew)"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    update.describe {
        tag("RallyEntries")
        summary = "Update rallyEntry"
        requestBody {
            required = true
            schema = jsonSchema<RallyEntry>()
        }
        responses {
            HttpStatusCode.NoContent {
                description = "RallyEntry updated"
            }
            HttpStatusCode.NotFound {
                description = "RallyEntry not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid request: missing required rally_id/crew_id and/or one or more referenced IDs do not exist (rally, crew)"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    delete.describe {
        tag("RallyEntries")
        summary = "Delete rallyEntry"
        responses {
            HttpStatusCode.NoContent {
                description = "RallyEntry deleted"
            }
            HttpStatusCode.NotFound {
                description = "RallyEntry not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }
}

