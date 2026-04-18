package com.racing.data

import kotlinx.serialization.Serializable

@Serializable
data class Intensity(
    val intensity_id: Int? = null,
    val name: String
) {
}