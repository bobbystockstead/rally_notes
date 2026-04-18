package com.racing.routes.docs

import com.racing.data.Manufacturer
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun attachManufacturerOpenApi(
    getAll: Route,
    getById: Route,
    create: Route,
    update: Route,
    delete: Route,
) {
    getAll.describe {
        tag("Manufacturers")
        summary = "Get all manufacturers"
        responses {
            HttpStatusCode.OK {
                description = "List of manufacturer records"
                schema = jsonSchema<List<Manufacturer>>()
            }
        }
    }

    getById.describe {
        tag("Manufacturers")
        summary = "Get manufacturer by id"
        responses {
            HttpStatusCode.OK {
                description = "Manufacturer found"
                schema = jsonSchema<Manufacturer>()
            }
            HttpStatusCode.NotFound {
                description = "Manufacturer not found"
            }
        }
    }

    create.describe {
        tag("Manufacturers")
        summary = "Create manufacturer"
        requestBody {
            required = true
            schema = jsonSchema<Manufacturer>()
        }
        responses {
            HttpStatusCode.Created {
                description = "Manufacturer created"
            }
        }
    }

    update.describe {
        tag("Manufacturers")
        summary = "Update manufacturer"
        requestBody {
            required = true
            schema = jsonSchema<Manufacturer>()
        }
        responses {
            HttpStatusCode.NoContent {
                description = "Manufacturer updated"
            }
            HttpStatusCode.NotFound {
                description = "Manufacturer not found"
            }
        }
    }

    delete.describe {
        tag("Manufacturers")
        summary = "Delete manufacturer"
        responses {
            HttpStatusCode.NoContent {
                description = "Manufacturer deleted"
            }
            HttpStatusCode.NotFound {
                description = "Manufacturer not found"
            }
        }
    }
}

