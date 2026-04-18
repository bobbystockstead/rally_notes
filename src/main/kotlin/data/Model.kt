package com.racing.data

import kotlinx.serialization.Serializable

@Serializable
data class Model(
    val model_id: Int? = null,
    val name: String,
    val manufacturer_id: Int? = null
) {
}