package com.racing.data

import kotlinx.serialization.Serializable

@Serializable
data class Crew(
    val crew_id: Int? = null,
    val driver_id: Int? = null,
    val codriver_id: Int? = null,
    val car_id: Int? = null,
    val team_id: Int? = null
) {
}