package com.racing.routes.docs

import com.racing.data.Tip
import com.racing.data.ValidationErrorResponse
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun attachTipOpenApi(
    getAll: Route,
    getById: Route,
    create: Route,
    update: Route,
    delete: Route,
) {
    getAll.describe {
        tag("Tips")
        summary = "Get all tips"
        responses {
            HttpStatusCode.OK {
                description = "List of tip records"
                schema = jsonSchema<List<Tip>>()
            }
        }
    }

    getById.describe {
        tag("Tips")
        summary = "Get tip by id"
        responses {
            HttpStatusCode.OK {
                description = "Tip found"
                schema = jsonSchema<Tip>()
            }
            HttpStatusCode.NotFound {
                description = "Tip not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    create.describe {
        tag("Tips")
        summary = "Create tip"
        requestBody {
            required = true
            schema = jsonSchema<Tip>()
        }
        responses {
            HttpStatusCode.Created {
                description = "Tip created"
            }
        }
    }

    update.describe {
        tag("Tips")
        summary = "Update tip"
        requestBody {
            required = true
            schema = jsonSchema<Tip>()
        }
        responses {
            HttpStatusCode.NoContent {
                description = "Tip updated"
            }
            HttpStatusCode.NotFound {
                description = "Tip not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    delete.describe {
        tag("Tips")
        summary = "Delete tip"
        responses {
            HttpStatusCode.NoContent {
                description = "Tip deleted"
            }
            HttpStatusCode.NotFound {
                description = "Tip not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }
}

