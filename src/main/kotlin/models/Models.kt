package com.racer.models

import kotlinx.serialization.Serializable

/**
 * Domain models for Rally Notes Application.
 * Based on the database schema for managing rally competitions.
 */

// ==================== Rally Models ====================
@Serializable
data class Rally(
    val rallyId: Int,
    val name: String,
    val date: String?
)

@Serializable
data class CreateRallyRequest(
    val name: String,
    val date: String? = null
)

@Serializable
data class UpdateRallyRequest(
    val name: String? = null,
    val date: String? = null
)

// ==================== Stage Models ====================
@Serializable
data class Stage(
    val stageId: Int,
    val name: String,
    val distance: Double?
)

@Serializable
data class CreateStageRequest(
    val name: String,
    val distance: Double? = null
)

@Serializable
data class UpdateStageRequest(
    val name: String? = null,
    val distance: Double? = null
)

// ==================== Driver Models ====================
@Serializable
data class Driver(
    val driverId: Int,
    val name: String,
    val number: Int?
)

@Serializable
data class CreateDriverRequest(
    val name: String,
    val number: Int? = null
)

@Serializable
data class UpdateDriverRequest(
    val name: String? = null,
    val number: Int? = null
)

// ==================== Team Models ====================
@Serializable
data class Team(
    val teamId: Int,
    val name: String,
    val driverId: Int?,
    val coDriverId: Int?,
    val carId: Int?,
    val manufacturerId: Int?
)

@Serializable
data class CreateTeamRequest(
    val name: String,
    val driverId: Int? = null,
    val coDriverId: Int? = null,
    val carId: Int? = null,
    val manufacturerId: Int? = null
)

@Serializable
data class UpdateTeamRequest(
    val name: String? = null,
    val driverId: Int? = null,
    val coDriverId: Int? = null,
    val carId: Int? = null,
    val manufacturerId: Int? = null
)

// ==================== Car Models ====================
@Serializable
data class Car(
    val carId: Int,
    val name: String,
    val modelId: Int?
)

@Serializable
data class CreateCarRequest(
    val name: String,
    val modelId: Int? = null
)

@Serializable
data class UpdateCarRequest(
    val name: String? = null,
    val modelId: Int? = null
)

// ==================== Manufacturer Models ====================
@Serializable
data class Manufacturer(
    val manufacturerId: Int,
    val name: String
)

@Serializable
data class CreateManufacturerRequest(
    val name: String
)

@Serializable
data class UpdateManufacturerRequest(
    val name: String? = null
)

// ==================== Model Models ====================
@Serializable
data class Model(
    val modelId: Int,
    val name: String,
    val manufacturerId: Int
)

@Serializable
data class CreateModelRequest(
    val name: String,
    val manufacturerId: Int
)

@Serializable
data class UpdateModelRequest(
    val name: String? = null,
    val manufacturerId: Int? = null
)

// ==================== Intensity Models ====================
@Serializable
data class Intensity(
    val intensityId: Int,
    val name: String
)

@Serializable
data class CreateIntensityRequest(
    val name: String
)

// ==================== Warning Models ====================
@Serializable
data class Warning(
    val warningId: Int,
    val description: String
)

@Serializable
data class CreateWarningRequest(
    val description: String
)

// ==================== Tip Models ====================
@Serializable
data class Tip(
    val tipId: Int,
    val description: String
)

@Serializable
data class CreateTipRequest(
    val description: String
)

// ==================== Call Models ====================
@Serializable
data class Call(
    val callId: Int,
    val stageId: Int,
    val orderInStage: Int,
    val gear: String?,
    val direction: String?,
    val intensityId: Int?,
    val warningId: Int?,
    val tipId: Int?
)

@Serializable
data class CreateCallRequest(
    val stageId: Int,
    val orderInStage: Int,
    val gear: String? = null,
    val direction: String? = null,
    val intensityId: Int? = null,
    val warningId: Int? = null,
    val tipId: Int? = null
)

@Serializable
data class UpdateCallRequest(
    val gear: String? = null,
    val direction: String? = null,
    val intensityId: Int? = null,
    val warningId: Int? = null,
    val tipId: Int? = null
)

// ==================== Rally to Team Models ====================
@Serializable
data class RallyToTeam(
    val rallyToTeamId: Int,
    val rallyId: Int,
    val teamId: Int
)

@Serializable
data class CreateRallyToTeamRequest(
    val rallyId: Int,
    val teamId: Int
)

// ==================== Rally Stage Map Models ====================
@Serializable
data class RallyStageMap(
    val stageToRallyId: Int,
    val stageId: Int,
    val rallyId: Int,
    val stageOrder: Int
)

@Serializable
data class CreateRallyStageMapRequest(
    val stageId: Int,
    val rallyId: Int,
    val stageOrder: Int
)

// ==================== Generic Response Models ====================
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null
)

@Serializable
data class PaginatedResponse<T>(
    val items: List<T>,
    val total: Long,
    val limit: Int,
    val offset: Int
)


