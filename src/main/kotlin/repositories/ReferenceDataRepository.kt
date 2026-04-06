package com.racer.repositories

import com.racer.models.Intensity
import com.racer.models.Warning
import com.racer.models.Tip
import com.racer.database.DatabaseFactory
import javax.sql.DataSource
import java.sql.ResultSet

/**
 * Repository for Intensity entities (e.g., light, medium, hard).
 */
class IntensityRepository : BaseRepository {
    override val dataSource: DataSource
        get() = DatabaseFactory.getDataSource()

    companion object {
        private const val SELECT_ALL = "SELECT intensity_id, name FROM intensity"
        private const val SELECT_BY_ID = "SELECT intensity_id, name FROM intensity WHERE intensity_id = "
        private const val INSERT = "INSERT INTO intensity (name) VALUES "
        private const val UPDATE_BY_ID = "UPDATE intensity SET name = '%s' WHERE intensity_id = %d"
        private const val DELETE_BY_ID = "DELETE FROM intensity WHERE intensity_id = "

        private fun mapRowToIntensity(rs: ResultSet): Intensity = Intensity(
            intensityId = rs.getInt("intensity_id"),
            name = rs.getString("name")
        )
    }

    fun findAll(): List<Intensity> = queryForList(SELECT_ALL) { mapRowToIntensity(it) }

    fun findById(id: Int): Intensity =
        queryForObject("$SELECT_BY_ID$id") { mapRowToIntensity(it) }
            ?: throw RepositoryException.NotFoundException("Intensity with id $id not found")

    fun create(name: String): Int {
        val escapedName = name.replace("'", "\\'")
        val sql = "$INSERT ('$escapedName')"

        return withConnection { conn ->
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.executeUpdate()
                stmt.generatedKeys.use { rs ->
                    if (rs.next()) rs.getInt(1) else throw RepositoryException.DataAccessException("Failed to retrieve generated id")
                }
            }
        }
    }

    fun update(id: Int, name: String): Boolean {
        val escapedName = name.replace("'", "\\'")
        val sql = String.format(UPDATE_BY_ID, escapedName, id)
        return update(sql) > 0
    }

    fun delete(id: Int): Boolean = update("$DELETE_BY_ID$id") > 0
}

/**
 * Repository for Warning entities.
 */
class WarningRepository : BaseRepository {
    override val dataSource: DataSource
        get() = DatabaseFactory.getDataSource()

    companion object {
        private const val SELECT_ALL = "SELECT warning_id, description FROM warning"
        private const val SELECT_BY_ID = "SELECT warning_id, description FROM warning WHERE warning_id = "
        private const val INSERT = "INSERT INTO warning (description) VALUES "
        private const val UPDATE_BY_ID = "UPDATE warning SET description = '%s' WHERE warning_id = %d"
        private const val DELETE_BY_ID = "DELETE FROM warning WHERE warning_id = "

        private fun mapRowToWarning(rs: ResultSet): Warning = Warning(
            warningId = rs.getInt("warning_id"),
            description = rs.getString("description")
        )
    }

    fun findAll(): List<Warning> = queryForList(SELECT_ALL) { mapRowToWarning(it) }

    fun findById(id: Int): Warning =
        queryForObject("$SELECT_BY_ID$id") { mapRowToWarning(it) }
            ?: throw RepositoryException.NotFoundException("Warning with id $id not found")

    fun create(description: String): Int {
        val escapedDesc = description.replace("'", "\\'")
        val sql = "$INSERT ('$escapedDesc')"

        return withConnection { conn ->
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.executeUpdate()
                stmt.generatedKeys.use { rs ->
                    if (rs.next()) rs.getInt(1) else throw RepositoryException.DataAccessException("Failed to retrieve generated id")
                }
            }
        }
    }

    fun update(id: Int, description: String): Boolean {
        val escapedDesc = description.replace("'", "\\'")
        val sql = String.format(UPDATE_BY_ID, escapedDesc, id)
        return update(sql) > 0
    }

    fun delete(id: Int): Boolean = update("$DELETE_BY_ID$id") > 0
}

/**
 * Repository for Tip entities.
 */
class TipRepository : BaseRepository {
    override val dataSource: DataSource
        get() = DatabaseFactory.getDataSource()

    companion object {
        private const val SELECT_ALL = "SELECT tip_id, description FROM tip"
        private const val SELECT_BY_ID = "SELECT tip_id, description FROM tip WHERE tip_id = "
        private const val INSERT = "INSERT INTO tip (description) VALUES "
        private const val UPDATE_BY_ID = "UPDATE tip SET description = '%s' WHERE tip_id = %d"
        private const val DELETE_BY_ID = "DELETE FROM tip WHERE tip_id = "

        private fun mapRowToTip(rs: ResultSet): Tip = Tip(
            tipId = rs.getInt("tip_id"),
            description = rs.getString("description")
        )
    }

    fun findAll(): List<Tip> = queryForList(SELECT_ALL) { mapRowToTip(it) }

    fun findById(id: Int): Tip =
        queryForObject("$SELECT_BY_ID$id") { mapRowToTip(it) }
            ?: throw RepositoryException.NotFoundException("Tip with id $id not found")

    fun create(description: String): Int {
        val escapedDesc = description.replace("'", "\\'")
        val sql = "$INSERT ('$escapedDesc')"

        return withConnection { conn ->
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.executeUpdate()
                stmt.generatedKeys.use { rs ->
                    if (rs.next()) rs.getInt(1) else throw RepositoryException.DataAccessException("Failed to retrieve generated id")
                }
            }
        }
    }

    fun update(id: Int, description: String): Boolean {
        val escapedDesc = description.replace("'", "\\'")
        val sql = String.format(UPDATE_BY_ID, escapedDesc, id)
        return update(sql) > 0
    }

    fun delete(id: Int): Boolean = update("$DELETE_BY_ID$id") > 0
}

