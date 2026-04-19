package com.racing.data

import kotlinx.serialization.Serializable

@Serializable
data class RallyStageMap(
    val rally_stage_id: Int? = null,
    val rally_id: Int? = null,
    val stage_id: Int? = null,
    val stage_order: Int? = null
) {
}