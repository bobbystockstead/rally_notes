package com.racer.repositories

import com.racer.models.*
import com.racer.database.DatabaseFactory
import javax.sql.DataSource
import java.sql.ResultSet

/**
 * Repository for Rally entities.
 * Handles all database operations for rallies using raw SQL.
 */
class RallyRepository : BaseRepository {
    override val dataSource: DataSource
        get() = DatabaseFactory.getDataSource()

    companion object {
        private const val SELECT_ALL = "SELECT rally_id, name, date FROM rally"
        private const val SELECT_BY_ID = "SELECT rally_id, name, date FROM rally WHERE rally_id = "
        private const val INSERT = "INSERT INTO rally (name, date) VALUES "
        private const val UPDATE_BY_ID = "UPDATE rally SET name = '%s', date = %s WHERE rally_id = %d"
        private const val DELETE_BY_ID = "DELETE FROM rally WHERE rally_id = "

        private fun mapRowToRally(rs: ResultSet): Rally = Rally(
            rallyId = rs.getInt("rally_id"),
            name = rs.getString("name"),
            date = rs.getString("date")
        )
    }

    fun findAll(): List<Rally> {
        return queryForList(SELECT_ALL) { mapRowToRally(it) }
    }

    fun findById(id: Int): Rally {
        return queryForObject("$SELECT_BY_ID$id") { mapRowToRally(it) }
            ?: throw RepositoryException.NotFoundException("Rally with id $id not found")
    }

    fun create(name: String, date: String?): Int {
        val escapedName = name.replace("'", "\\'")
        val dateValue = if (date != null) "'$date'" else "NULL"
        val sql = "$INSERT ('$escapedName', $dateValue)"

        return withConnection { conn ->
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.executeUpdate()
                stmt.generatedKeys.use { rs ->
                    if (rs.next()) rs.getInt(1) else throw RepositoryException.DataAccessException("Failed to retrieve generated id")
                }
            }
        }
    }

    fun update(id: Int, name: String, date: String?): Boolean {
        val escapedName = name.replace("'", "\\'")
        val dateValue = if (date != null) "'$date'" else "NULL"
        val sql = String.format(UPDATE_BY_ID, escapedName, dateValue, id)
        return update(sql) > 0
    }

    fun delete(id: Int): Boolean {
        return update("$DELETE_BY_ID$id") > 0
    }
}


