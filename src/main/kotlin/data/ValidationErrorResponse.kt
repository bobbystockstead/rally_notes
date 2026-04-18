package com.racing.data

import kotlinx.serialization.Serializable

@Serializable
data class ValidationErrorResponse(
    val error: String = "Validation failed",
    val details: List<ValidationIssue>
)