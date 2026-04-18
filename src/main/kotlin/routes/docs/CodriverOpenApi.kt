package com.racing.routes.docs

import com.racing.data.Codriver
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun attachCodriverOpenApi(
    getAll: Route,
    getById: Route,
    create: Route,
    update: Route,
    delete: Route,
) {
    getAll.describe {
        tag("Codrivers")
        summary = "Get all codrivers"
        responses {
            HttpStatusCode.OK {
                description = "List of codriver records"
                schema = jsonSchema<List<Codriver>>()
            }
        }
    }

    getById.describe {
        tag("Codrivers")
        summary = "Get codriver by id"
        responses {
            HttpStatusCode.OK {
                description = "Codriver found"
                schema = jsonSchema<Codriver>()
            }
            HttpStatusCode.NotFound {
                description = "Codriver not found"
            }
        }
    }

    create.describe {
        tag("Codrivers")
        summary = "Create codriver"
        requestBody {
            required = true
            schema = jsonSchema<Codriver>()
        }
        responses {
            HttpStatusCode.Created {
                description = "Codriver created"
            }
        }
    }

    update.describe {
        tag("Codrivers")
        summary = "Update codriver"
        requestBody {
            required = true
            schema = jsonSchema<Codriver>()
        }
        responses {
            HttpStatusCode.NoContent {
                description = "Codriver updated"
            }
            HttpStatusCode.NotFound {
                description = "Codriver not found"
            }
        }
    }

    delete.describe {
        tag("Codrivers")
        summary = "Delete codriver"
        responses {
            HttpStatusCode.NoContent {
                description = "Codriver deleted"
            }
            HttpStatusCode.NotFound {
                description = "Codriver not found"
            }
        }
    }
}

