package com.racing.data

import kotlinx.serialization.Serializable

@Serializable
data class Driver(
    val driver_id: Int? = null,
    val name: String,
    val number: Int? = null
) {
}