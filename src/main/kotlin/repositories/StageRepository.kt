package com.racer.repositories

import com.racer.models.Stage
import com.racer.database.DatabaseFactory
import javax.sql.DataSource
import java.sql.ResultSet

/**
 * Repository for Stage entities.
 */
class StageRepository : BaseRepository {
    override val dataSource: DataSource
        get() = DatabaseFactory.getDataSource()

    companion object {
        private const val SELECT_ALL = "SELECT stage_id, name, distance FROM stage"
        private const val SELECT_BY_ID = "SELECT stage_id, name, distance FROM stage WHERE stage_id = "
        private const val INSERT = "INSERT INTO stage (name, distance) VALUES "
        private const val UPDATE_BY_ID = "UPDATE stage SET name = '%s', distance = %s WHERE stage_id = %d"
        private const val DELETE_BY_ID = "DELETE FROM stage WHERE stage_id = "

        private fun mapRowToStage(rs: ResultSet): Stage = Stage(
            stageId = rs.getInt("stage_id"),
            name = rs.getString("name"),
            distance = rs.getObject("distance") as? Double
        )
    }

    fun findAll(): List<Stage> = queryForList(SELECT_ALL) { mapRowToStage(it) }

    fun findById(id: Int): Stage =
        queryForObject("$SELECT_BY_ID$id") { mapRowToStage(it) }
            ?: throw RepositoryException.NotFoundException("Stage with id $id not found")

    fun create(name: String, distance: Double?): Int {
        val escapedName = name.replace("'", "\\'")
        val distanceValue = if (distance != null) "$distance" else "NULL"
        val sql = "$INSERT ('$escapedName', $distanceValue)"

        return withConnection { conn ->
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.executeUpdate()
                stmt.generatedKeys.use { rs ->
                    if (rs.next()) rs.getInt(1) else throw RepositoryException.DataAccessException("Failed to retrieve generated id")
                }
            }
        }
    }

    fun update(id: Int, name: String, distance: Double?): Boolean {
        val escapedName = name.replace("'", "\\'")
        val distanceValue = if (distance != null) "$distance" else "NULL"
        val sql = String.format(UPDATE_BY_ID, escapedName, distanceValue, id)
        return update(sql) > 0
    }

    fun delete(id: Int): Boolean = update("$DELETE_BY_ID$id") > 0
}

