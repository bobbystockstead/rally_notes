package com.racing.routes.docs

import com.racing.data.Car
import com.racing.data.ValidationErrorResponse
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun attachCarOpenApi(
    getAll: Route,
    getById: Route,
    create: Route,
    update: Route,
    delete: Route,
) {
    getAll.describe {
        tag("Cars")
        summary = "Get all cars"
        responses {
            HttpStatusCode.OK {
                description = "List of car records"
                schema = jsonSchema<List<Car>>()
            }
        }
    }

    getById.describe {
        tag("Cars")
        summary = "Get car by id"
        responses {
            HttpStatusCode.OK {
                description = "Car found"
                schema = jsonSchema<Car>()
            }
            HttpStatusCode.NotFound {
                description = "Car not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    create.describe {
        tag("Cars")
        summary = "Create car"
        requestBody {
            required = true
            schema = jsonSchema<Car>()
        }
        responses {
            HttpStatusCode.Created {
                description = "Car created"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid model_id - model does not exist"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    update.describe {
        tag("Cars")
        summary = "Update car"
        requestBody {
            required = true
            schema = jsonSchema<Car>()
        }
        responses {
            HttpStatusCode.NoContent {
                description = "Car updated"
            }
            HttpStatusCode.NotFound {
                description = "Car not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid request: id must be an integer or model_id does not exist"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    delete.describe {
        tag("Cars")
        summary = "Delete car"
        responses {
            HttpStatusCode.NoContent {
                description = "Car deleted"
            }
            HttpStatusCode.NotFound {
                description = "Car not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }
}

