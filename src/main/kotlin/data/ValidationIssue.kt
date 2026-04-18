package com.racing.data

import kotlinx.serialization.Serializable

@Serializable
data class ValidationIssue(
    val field: String,
    val message: String
)
