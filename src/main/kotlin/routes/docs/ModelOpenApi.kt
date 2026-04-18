package com.racing.routes.docs

import com.racing.data.Model
import com.racing.data.ValidationErrorResponse
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun attachModelOpenApi(
    getAll: Route,
    getById: Route,
    create: Route,
    update: Route,
    delete: Route,
) {
    getAll.describe {
        tag("Models")
        summary = "Get all models"
        responses {
            HttpStatusCode.OK {
                description = "List of model records"
                schema = jsonSchema<List<Model>>()
            }
        }
    }

    getById.describe {
        tag("Models")
        summary = "Get model by id"
        responses {
            HttpStatusCode.OK {
                description = "Model found"
                schema = jsonSchema<Model>()
            }
            HttpStatusCode.NotFound {
                description = "Model not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    create.describe {
        tag("Models")
        summary = "Create model"
        requestBody {
            required = true
            schema = jsonSchema<Model>()
        }
        responses {
            HttpStatusCode.Created {
                description = "Model created"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid manufacturer_id - manufacturer does not exist"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    update.describe {
        tag("Models")
        summary = "Update model"
        requestBody {
            required = true
            schema = jsonSchema<Model>()
        }
        responses {
            HttpStatusCode.NoContent {
                description = "Model updated"
            }
            HttpStatusCode.NotFound {
                description = "Model not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid request: id must be an integer or manufacturer_id does not exist"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    delete.describe {
        tag("Models")
        summary = "Delete model"
        responses {
            HttpStatusCode.NoContent {
                description = "Model deleted"
            }
            HttpStatusCode.NotFound {
                description = "Model not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }
}

