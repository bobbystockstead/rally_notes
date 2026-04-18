package com.racing.routes.docs

import com.racing.data.Call
import com.racing.data.ValidationErrorResponse
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun attachCallOpenApi(
    getAll: Route,
    getById: Route,
    create: Route,
    update: Route,
    delete: Route,
) {
    getAll.describe {
        tag("Calls")
        summary = "Get all calls"
        responses {
            HttpStatusCode.OK {
                description = "List of call records"
                schema = jsonSchema<List<Call>>()
            }
        }
    }

    getById.describe {
        tag("Calls")
        summary = "Get call by id"
        responses {
            HttpStatusCode.OK {
                description = "Call found"
                schema = jsonSchema<Call>()
            }
            HttpStatusCode.NotFound {
                description = "Call not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    create.describe {
        tag("Calls")
        summary = "Create call"
        requestBody {
            required = true
            schema = jsonSchema<Call>()
        }
        responses {
            HttpStatusCode.Created {
                description = "Call created"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid request: missing required note_id/sequence_number and/or one or more referenced IDs do not exist (note, intensity, warning, tip)"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    update.describe {
        tag("Calls")
        summary = "Update call"
        requestBody {
            required = true
            schema = jsonSchema<Call>()
        }
        responses {
            HttpStatusCode.NoContent {
                description = "Call updated"
            }
            HttpStatusCode.NotFound {
                description = "Call not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid request: id must be an integer or one or more references do not exist (note, intensity, warning, tip)"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    delete.describe {
        tag("Calls")
        summary = "Delete call"
        responses {
            HttpStatusCode.NoContent {
                description = "Call deleted"
            }
            HttpStatusCode.NotFound {
                description = "Call not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }
}

