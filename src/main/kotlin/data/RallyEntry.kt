package com.racing.data

import kotlinx.serialization.Serializable

@Serializable
data class RallyEntry(
    val entry_id: Int? = null,
    val rally_id: Int? = null,
    val crew_id: Int? = null,
    val car_number: Int? = null
) {
}