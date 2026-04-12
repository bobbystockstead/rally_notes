package com.racing.data

@kotlinx.serialization.Serializable
data class Team(
    val id: Int,
    val name: String)