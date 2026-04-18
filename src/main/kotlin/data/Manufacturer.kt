package com.racing.data

import kotlinx.serialization.Serializable

@Serializable
data class Manufacturer(
    val manufacturer_id: Int? = null,
    val name: String
) {
}