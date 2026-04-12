package com.racing.data

import kotlinx.datetime.LocalDate

@kotlinx.serialization.Serializable
data class Rally(
    val rally_id: Int? = null,
    val name: String,
    val date: LocalDate
) {
}