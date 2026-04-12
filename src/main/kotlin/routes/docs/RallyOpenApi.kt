package com.racing.routes.docs

import com.racing.data.Rally
import io.ktor.http.HttpStatusCode
import io.ktor.openapi.jsonSchema
import io.ktor.server.routing.Route
import io.ktor.server.routing.openapi.describe
import io.ktor.utils.io.ExperimentalKtorApi

@OptIn(ExperimentalKtorApi::class)
fun attachRallyOpenApi(
    getAll: Route,
    getById: Route,
    create: Route,
    update: Route,
    delete: Route,
) {
    getAll.describe {
        tag("Rallies")
        summary = "Get all rallies"
        responses {
            HttpStatusCode.OK {
                description = "List of rally records"
                schema = jsonSchema<List<Rally>>()
            }
        }
    }

    getById.describe {
        tag("Rallies")
        summary = "Get rally by id"
        responses {
            HttpStatusCode.OK {
                description = "Rally found"
                schema = jsonSchema<Rally>()
            }
            HttpStatusCode.NotFound {
                description = "Rally not found"
            }
        }
    }

    create.describe {
        tag("Rallies")
        summary = "Create rally"
        requestBody {
            required = true
            schema = jsonSchema<Rally>()
        }
        responses {
            HttpStatusCode.Created {
                description = "Rally created"
            }
        }
    }

    update.describe {
        tag("Rallies")
        summary = "Update rally"
        requestBody {
            required = true
            schema = jsonSchema<Rally>()
        }
        responses {
            HttpStatusCode.NoContent {
                description = "Rally updated"
            }
            HttpStatusCode.NotFound {
                description = "Rally not found"
            }
        }
    }

    delete.describe {
        tag("Rallies")
        summary = "Delete rally"
        responses {
            HttpStatusCode.NoContent {
                description = "Rally deleted"
            }
            HttpStatusCode.NotFound {
                description = "Rally not found"
            }
        }
    }
}

