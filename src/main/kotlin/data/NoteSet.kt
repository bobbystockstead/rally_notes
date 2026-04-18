package com.racing.data

import kotlinx.serialization.Serializable

@Serializable
data class NoteSet(
    val note_id: Int? = null,
    val crew_id: Int? = null,
    val name: String? = null,
    val stage_id: Int? = null,
    val conditions: String? = null
) {
}