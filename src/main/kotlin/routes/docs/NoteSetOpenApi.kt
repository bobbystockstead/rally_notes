package com.racing.routes.docs

import com.racing.data.NoteSet
import com.racing.data.ValidationErrorResponse
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun attachNoteSetOpenApi(
    getAll: Route,
    getById: Route,
    create: Route,
    update: Route,
    delete: Route,
) {
    getAll.describe {
        tag("NoteSets")
        summary = "Get all NoteSets"
        responses {
            HttpStatusCode.OK {
                description = "List of NoteSet records"
                schema = jsonSchema<List<NoteSet>>()
            }
        }
    }

    getById.describe {
        tag("NoteSets")
        summary = "Get NoteSet by id"
        responses {
            HttpStatusCode.OK {
                description = "NoteSet found"
                schema = jsonSchema<NoteSet>()
            }
            HttpStatusCode.NotFound {
                description = "NoteSet not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    create.describe {
        tag("NoteSets")
        summary = "Create noteSet"
        requestBody {
            required = true
            schema = jsonSchema<NoteSet>()
        }
        responses {
            HttpStatusCode.Created {
                description = "NoteSet created"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid request: missing required crew_id/stage_id and/or one or more referenced IDs do not exist (crew, stage)"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    update.describe {
        tag("NoteSets")
        summary = "Update noteSet"
        requestBody {
            required = true
            schema = jsonSchema<NoteSet>()
        }
        responses {
            HttpStatusCode.NoContent {
                description = "NoteSet updated"
            }
            HttpStatusCode.NotFound {
                description = "NoteSet not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid request: missing required crew_id/stage_id and/or one or more referenced IDs do not exist (crew, stage)"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }

    delete.describe {
        tag("NoteSets")
        summary = "Delete noteSet"
        responses {
            HttpStatusCode.NoContent {
                description = "NoteSet deleted"
            }
            HttpStatusCode.NotFound {
                description = "NoteSet not found"
            }
            HttpStatusCode.BadRequest {
                description = "Invalid path parameter: id must be an integer"
                schema = jsonSchema<ValidationErrorResponse>()
            }
        }
    }
}

