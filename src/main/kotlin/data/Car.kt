package com.racing.data

import kotlinx.serialization.Serializable

@Serializable
data class Car(
    val car_id: Int? = null,
    val name: String,
    val model_id: Int? = null
) {
}