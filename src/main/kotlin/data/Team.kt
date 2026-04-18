package com.racing.data

import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val team_id: Int? = null,
    val name: String,
    val manufacturer_id: Int? = null
) {
}