package com.racing.routes.docs

import com.racing.data.Warning
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun attachWarningOpenApi(
    getAll: Route,
    getById: Route,
    create: Route,
    update: Route,
    delete: Route,
) {
    getAll.describe {
        tag("Warnings")
        summary = "Get all warnings"
        responses {
            HttpStatusCode.OK {
                description = "List of warning records"
                schema = jsonSchema<List<Warning>>()
            }
        }
    }

    getById.describe {
        tag("Warnings")
        summary = "Get warning by id"
        responses {
            HttpStatusCode.OK {
                description = "Warning found"
                schema = jsonSchema<Warning>()
            }
            HttpStatusCode.NotFound {
                description = "Warning not found"
            }
        }
    }

    create.describe {
        tag("Warnings")
        summary = "Create warning"
        requestBody {
            required = true
            schema = jsonSchema<Warning>()
        }
        responses {
            HttpStatusCode.Created {
                description = "Warning created"
            }
        }
    }

    update.describe {
        tag("Warnings")
        summary = "Update warning"
        requestBody {
            required = true
            schema = jsonSchema<Warning>()
        }
        responses {
            HttpStatusCode.NoContent {
                description = "Warning updated"
            }
            HttpStatusCode.NotFound {
                description = "Warning not found"
            }
        }
    }

    delete.describe {
        tag("Warnings")
        summary = "Delete warning"
        responses {
            HttpStatusCode.NoContent {
                description = "Warning deleted"
            }
            HttpStatusCode.NotFound {
                description = "Warning not found"
            }
        }
    }
}

