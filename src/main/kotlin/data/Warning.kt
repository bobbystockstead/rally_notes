package com.racing.data

import kotlinx.serialization.Serializable

@Serializable
data class Warning(
    val warning_id: Int? = null,
    val description: String
) {
}