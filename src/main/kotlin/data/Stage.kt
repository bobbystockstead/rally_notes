package com.racing.data

import kotlinx.serialization.Serializable

@Serializable
data class Stage(
    val stage_id: Int? = null,
    val name: String,
    val distance: Double? = null
) {
}