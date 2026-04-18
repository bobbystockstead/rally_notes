package com.racing.routes

import com.racing.data.ValidationErrorResponse
import com.racing.data.ValidationIssue
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import org.slf4j.Logger

suspend fun ApplicationCall.parsePathIdOrRespond(
    logger: Logger,
    parameterName: String = "id"
): Int? {
    val id = parameters[parameterName]?.toIntOrNull()
    if (id == null) {
        logger.warn("Request rejected - invalid path parameter: {}", parameterName)
        respond(
            HttpStatusCode.BadRequest,
            ValidationErrorResponse(
                details = listOf(
                    ValidationIssue(parameterName, "Path parameter '$parameterName' must be an integer")
                )
            )
        )
    }
    return id
}

