package com.racer.repositories

import com.racer.models.Call
import com.racer.models.RallyToTeam
import com.racer.models.RallyStageMap
import com.racer.database.DatabaseFactory
import javax.sql.DataSource
import java.sql.ResultSet

/**
 * Repository for Call entities (driving instructions within a stage).
 */
class CallRepository : BaseRepository {
    override val dataSource: DataSource
        get() = DatabaseFactory.getDataSource()

    companion object {
        private const val SELECT_ALL = "SELECT call_id, stage_id, order_in_stage, gear, direction, intensity_id, warning_id, tip_id FROM call"
        private const val SELECT_BY_ID = "SELECT call_id, stage_id, order_in_stage, gear, direction, intensity_id, warning_id, tip_id FROM call WHERE call_id = "
        private const val SELECT_BY_STAGE = "SELECT call_id, stage_id, order_in_stage, gear, direction, intensity_id, warning_id, tip_id FROM call WHERE stage_id = %d ORDER BY order_in_stage"
        private const val INSERT = "INSERT INTO call (stage_id, order_in_stage, gear, direction, intensity_id, warning_id, tip_id) VALUES "
        private const val UPDATE_BY_ID = "UPDATE call SET gear = '%s', direction = '%s', intensity_id = %s, warning_id = %s, tip_id = %s WHERE call_id = %d"
        private const val DELETE_BY_ID = "DELETE FROM call WHERE call_id = "

        private fun mapRowToCall(rs: ResultSet): Call = Call(
            callId = rs.getInt("call_id"),
            stageId = rs.getInt("stage_id"),
            orderInStage = rs.getInt("order_in_stage"),
            gear = rs.getString("gear"),
            direction = rs.getString("direction"),
            intensityId = rs.getObject("intensity_id") as? Int,
            warningId = rs.getObject("warning_id") as? Int,
            tipId = rs.getObject("tip_id") as? Int
        )
    }

    fun findAll(): List<Call> = queryForList(SELECT_ALL) { mapRowToCall(it) }

    fun findById(id: Int): Call =
        queryForObject("$SELECT_BY_ID$id") { mapRowToCall(it) }
            ?: throw RepositoryException.NotFoundException("Call with id $id not found")

    fun findByStage(stageId: Int): List<Call> =
        queryForList(String.format(SELECT_BY_STAGE, stageId)) { mapRowToCall(it) }

    fun create(stageId: Int, orderInStage: Int, gear: String?, direction: String?, intensityId: Int?, warningId: Int?, tipId: Int?): Int {
        val gearVal = gear?.let { "'${it.replace("'", "\\'")}" } ?: "NULL"
        val directionVal = direction?.let { "'${it.replace("'", "\\'")}" } ?: "NULL"
        val intensityIdVal = intensityId?.toString() ?: "NULL"
        val warningIdVal = warningId?.toString() ?: "NULL"
        val tipIdVal = tipId?.toString() ?: "NULL"
        val sql = "$INSERT ($stageId, $orderInStage, $gearVal, $directionVal, $intensityIdVal, $warningIdVal, $tipIdVal)"

        return withConnection { conn ->
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.executeUpdate()
                stmt.generatedKeys.use { rs ->
                    if (rs.next()) rs.getInt(1) else throw RepositoryException.DataAccessException("Failed to retrieve generated id")
                }
            }
        }
    }

    fun update(id: Int, gear: String?, direction: String?, intensityId: Int?, warningId: Int?, tipId: Int?): Boolean {
        val gearVal = gear?.let { "'${it.replace("'", "\\'")}" } ?: "NULL"
        val directionVal = direction?.let { "'${it.replace("'", "\\'")}" } ?: "NULL"
        val intensityIdVal = intensityId?.toString() ?: "NULL"
        val warningIdVal = warningId?.toString() ?: "NULL"
        val tipIdVal = tipId?.toString() ?: "NULL"
        val sql = String.format(UPDATE_BY_ID, gearVal.trim('\''), directionVal.trim('\''), intensityIdVal, warningIdVal, tipIdVal, id)
        return update(sql) > 0
    }

    fun delete(id: Int): Boolean = update("$DELETE_BY_ID$id") > 0
}

/**
 * Repository for RallyToTeam mapping entities.
 */
class RallyToTeamRepository : BaseRepository {
    override val dataSource: DataSource
        get() = DatabaseFactory.getDataSource()

    companion object {
        private const val SELECT_ALL = "SELECT rally_to_team_id, rally_id, team_id FROM rally_to_team"
        private const val SELECT_BY_ID = "SELECT rally_to_team_id, rally_id, team_id FROM rally_to_team WHERE rally_to_team_id = "
        private const val SELECT_BY_RALLY = "SELECT rally_to_team_id, rally_id, team_id FROM rally_to_team WHERE rally_id = "
        private const val INSERT = "INSERT INTO rally_to_team (rally_id, team_id) VALUES "
        private const val DELETE_BY_ID = "DELETE FROM rally_to_team WHERE rally_to_team_id = "

        private fun mapRowToRallyToTeam(rs: ResultSet): RallyToTeam = RallyToTeam(
            rallyToTeamId = rs.getInt("rally_to_team_id"),
            rallyId = rs.getInt("rally_id"),
            teamId = rs.getInt("team_id")
        )
    }

    fun findAll(): List<RallyToTeam> = queryForList(SELECT_ALL) { mapRowToRallyToTeam(it) }

    fun findById(id: Int): RallyToTeam =
        queryForObject("$SELECT_BY_ID$id") { mapRowToRallyToTeam(it) }
            ?: throw RepositoryException.NotFoundException("RallyToTeam with id $id not found")

    fun findByRally(rallyId: Int): List<RallyToTeam> =
        queryForList("$SELECT_BY_RALLY$rallyId") { mapRowToRallyToTeam(it) }

    fun create(rallyId: Int, teamId: Int): Int {
        val sql = "$INSERT ($rallyId, $teamId)"

        return withConnection { conn ->
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.executeUpdate()
                stmt.generatedKeys.use { rs ->
                    if (rs.next()) rs.getInt(1) else throw RepositoryException.DataAccessException("Failed to retrieve generated id")
                }
            }
        }
    }

    fun delete(id: Int): Boolean = update("$DELETE_BY_ID$id") > 0
}

/**
 * Repository for RallyStageMap mapping entities.
 */
class RallyStageMapRepository : BaseRepository {
    override val dataSource: DataSource
        get() = DatabaseFactory.getDataSource()

    companion object {
        private const val SELECT_ALL = "SELECT stage_to_rally_id, stage_id, rally_id, stage_order FROM rally_stage_map"
        private const val SELECT_BY_ID = "SELECT stage_to_rally_id, stage_id, rally_id, stage_order FROM rally_stage_map WHERE stage_to_rally_id = "
        private const val SELECT_BY_RALLY = "SELECT stage_to_rally_id, stage_id, rally_id, stage_order FROM rally_stage_map WHERE rally_id = %d ORDER BY stage_order"
        private const val INSERT = "INSERT INTO rally_stage_map (stage_id, rally_id, stage_order) VALUES "
        private const val UPDATE_BY_ID = "UPDATE rally_stage_map SET stage_order = %d WHERE stage_to_rally_id = %d"
        private const val DELETE_BY_ID = "DELETE FROM rally_stage_map WHERE stage_to_rally_id = "

        private fun mapRowToRallyStageMap(rs: ResultSet): RallyStageMap = RallyStageMap(
            stageToRallyId = rs.getInt("stage_to_rally_id"),
            stageId = rs.getInt("stage_id"),
            rallyId = rs.getInt("rally_id"),
            stageOrder = rs.getInt("stage_order")
        )
    }

    fun findAll(): List<RallyStageMap> = queryForList(SELECT_ALL) { mapRowToRallyStageMap(it) }

    fun findById(id: Int): RallyStageMap =
        queryForObject("$SELECT_BY_ID$id") { mapRowToRallyStageMap(it) }
            ?: throw RepositoryException.NotFoundException("RallyStageMap with id $id not found")

    fun findByRally(rallyId: Int): List<RallyStageMap> =
        queryForList(String.format(SELECT_BY_RALLY, rallyId)) { mapRowToRallyStageMap(it) }

    fun create(stageId: Int, rallyId: Int, stageOrder: Int): Int {
        val sql = "$INSERT ($stageId, $rallyId, $stageOrder)"

        return withConnection { conn ->
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.executeUpdate()
                stmt.generatedKeys.use { rs ->
                    if (rs.next()) rs.getInt(1) else throw RepositoryException.DataAccessException("Failed to retrieve generated id")
                }
            }
        }
    }

    fun update(id: Int, stageOrder: Int): Boolean {
        val sql = String.format(UPDATE_BY_ID, stageOrder, id)
        return update(sql) > 0
    }

    fun delete(id: Int): Boolean = update("$DELETE_BY_ID$id") > 0
}

