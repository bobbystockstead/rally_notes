package com.racing.routes.docs

import com.racing.data.Intensity
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun attachIntensityOpenApi(
    getAll: Route,
    getById: Route,
    create: Route,
    update: Route,
    delete: Route,
) {
    getAll.describe {
        tag("Intensities")
        summary = "Get all intensities"
        responses {
            HttpStatusCode.OK {
                description = "List of intensity records"
                schema = jsonSchema<List<Intensity>>()
            }
        }
    }

    getById.describe {
        tag("Intensities")
        summary = "Get intensity by id"
        responses {
            HttpStatusCode.OK {
                description = "Intensity found"
                schema = jsonSchema<Intensity>()
            }
            HttpStatusCode.NotFound {
                description = "Intensity not found"
            }
        }
    }

    create.describe {
        tag("Intensities")
        summary = "Create intensity"
        requestBody {
            required = true
            schema = jsonSchema<Intensity>()
        }
        responses {
            HttpStatusCode.Created {
                description = "Intensity created"
            }
        }
    }

    update.describe {
        tag("Intensities")
        summary = "Update intensity"
        requestBody {
            required = true
            schema = jsonSchema<Intensity>()
        }
        responses {
            HttpStatusCode.NoContent {
                description = "Intensity updated"
            }
            HttpStatusCode.NotFound {
                description = "Intensity not found"
            }
        }
    }

    delete.describe {
        tag("Intensities")
        summary = "Delete intensity"
        responses {
            HttpStatusCode.NoContent {
                description = "Intensity deleted"
            }
            HttpStatusCode.NotFound {
                description = "Intensity not found"
            }
        }
    }
}

