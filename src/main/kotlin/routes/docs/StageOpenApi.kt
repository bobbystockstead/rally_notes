package com.racing.routes.docs

import com.racing.data.Stage
import com.racing.data.ValidationErrorResponse
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun attachStageOpenApi(
    getAll: Route,
    getById: Route,
    create: Route,
    update: Route,
    delete: Route,
) {
    getAll.describe {
        tag("Stages")
        summary = "Get all stages"
        responses {
            HttpStatusCode.OK {
                description = "List of stage records"
                schema = jsonSchema<List<Stage>>()
            }
        }
    }

    getById.describe {
        tag("Stages")
        summary = "Get stage by id"
        responses {
            HttpStatusCode.OK {
                description = "Stage found"
                schema = jsonSchema<Stage>()
            }
            HttpStatusCode.NotFound {
                description = "Stage not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    create.describe {
        tag("Stages")
        summary = "Create stage"
        requestBody {
            required = true
            schema = jsonSchema<Stage>()
        }
        responses {
            HttpStatusCode.Created {
                description = "Stage created"
            }
        }
    }

    update.describe {
        tag("Stages")
        summary = "Update stage"
        requestBody {
            required = true
            schema = jsonSchema<Stage>()
        }
        responses {
            HttpStatusCode.NoContent {
                description = "Stage updated"
            }
            HttpStatusCode.NotFound {
                description = "Stage not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    delete.describe {
        tag("Stages")
        summary = "Delete stage"
        responses {
            HttpStatusCode.NoContent {
                description = "Stage deleted"
            }
            HttpStatusCode.NotFound {
                description = "Stage not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }
}

