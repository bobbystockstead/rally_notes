package com.racing.routes.docs

import com.racing.data.Crew
import com.racing.data.ValidationErrorResponse
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun attachCrewOpenApi(
    getAll: Route,
    getById: Route,
    create: Route,
    update: Route,
    delete: Route,
) {
    getAll.describe {
        tag("Crews")
        summary = "Get all crews"
        responses {
            HttpStatusCode.OK {
                description = "List of crew records"
                schema = jsonSchema<List<Crew>>()
            }
        }
    }

    getById.describe {
        tag("Crews")
        summary = "Get crew by id"
        responses {
            HttpStatusCode.OK {
                description = "Crew found"
                schema = jsonSchema<Crew>()
            }
            HttpStatusCode.NotFound {
                description = "Crew not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    create.describe {
        tag("Crews")
        summary = "Create crew"
        requestBody {
            required = true
            schema = jsonSchema<Crew>()
        }
        responses {
            HttpStatusCode.Created {
                description = "Crew created"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid request: missing required driver_id/codriver_id and/or one or more referenced IDs do not exist (driver, codriver, car, team)"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    update.describe {
        tag("Crews")
        summary = "Update crew"
        requestBody {
            required = true
            schema = jsonSchema<Crew>()
        }
        responses {
            HttpStatusCode.NoContent {
                description = "Crew updated"
            }
            HttpStatusCode.NotFound {
                description = "Crew not found"
            }
            HttpStatusCode.BadRequest {
                description = "Validation failed: one or more references does not exist (driver, codriver, car, team)"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    delete.describe {
        tag("Crews")
        summary = "Delete crew"
        responses {
            HttpStatusCode.NoContent {
                description = "Crew deleted"
            }
            HttpStatusCode.NotFound {
                description = "Crew not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }
}

