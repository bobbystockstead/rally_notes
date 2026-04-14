package com.racing.data

import kotlinx.serialization.Serializable

@Serializable
data class Codriver(
    val codriver_id: Int? = null,
    val name: String,
    val number: Int? = null
) {
}