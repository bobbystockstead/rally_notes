package com.racing.data

import kotlinx.serialization.Serializable

@Serializable
data class Call(
    val call_id: Int? = null,
    val note_id: Int? = null,
    val sequence_number: Int? = null,
    val gear: Int? = null,
    val direction: String? = null,
    val distance: Int? = null,
    val intensity_id: Int? = null,
    val warning_id: Int? = null,
    val tip_id: Int? = null
) {
}