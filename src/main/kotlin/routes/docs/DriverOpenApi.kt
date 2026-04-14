package com.racing.routes.docs

import com.racing.data.Driver
import io.ktor.http.HttpStatusCode
import io.ktor.openapi.jsonSchema
import io.ktor.server.routing.Route
import io.ktor.server.routing.openapi.describe
import io.ktor.utils.io.ExperimentalKtorApi

@OptIn(ExperimentalKtorApi::class)
fun attachDriverOpenApi(
    getAll: Route,
    getById: Route,
    create: Route,
    update: Route,
    delete: Route,
) {
    getAll.describe {
        tag("Drivers")
        summary = "Get all drivers"
        responses {
            HttpStatusCode.OK {
                description = "List of driver records"
                schema = jsonSchema<List<Driver>>()
            }
        }
    }

    getById.describe {
        tag("Drivers")
        summary = "Get driver by id"
        responses {
            HttpStatusCode.OK {
                description = "Driver found"
                schema = jsonSchema<Driver>()
            }
            HttpStatusCode.NotFound {
                description = "Driver not found"
            }
        }
    }

    create.describe {
        tag("Drivers")
        summary = "Create driver"
        requestBody {
            required = true
            schema = jsonSchema<Driver>()
        }
        responses {
            HttpStatusCode.Created {
                description = "Driver created"
            }
        }
    }

    update.describe {
        tag("Drivers")
        summary = "Update driver"
        requestBody {
            required = true
            schema = jsonSchema<Driver>()
        }
        responses {
            HttpStatusCode.NoContent {
                description = "Driver updated"
            }
            HttpStatusCode.NotFound {
                description = "Driver not found"
            }
        }
    }

    delete.describe {
        tag("Drivers")
        summary = "Delete driver"
        responses {
            HttpStatusCode.NoContent {
                description = "Driver deleted"
            }
            HttpStatusCode.NotFound {
                description = "Driver not found"
            }
        }
    }
}

