package com.racing.data

import kotlinx.serialization.Serializable

@Serializable
data class Tip(
    val tip_id: Int? = null,
    val description: String
) {
}