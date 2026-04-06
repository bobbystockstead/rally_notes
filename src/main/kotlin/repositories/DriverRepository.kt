package com.racer.repositories

import com.racer.models.*
import com.racer.database.DatabaseFactory
import javax.sql.DataSource
import java.sql.ResultSet

/**
 * Repository for Driver entities.
 */
class DriverRepository : BaseRepository {
    override val dataSource: DataSource
        get() = DatabaseFactory.getDataSource()

    companion object {
        private const val SELECT_ALL = "SELECT driver_id, name, number FROM driver"
        private const val SELECT_BY_ID = "SELECT driver_id, name, number FROM driver WHERE driver_id = "
        private const val INSERT = "INSERT INTO driver (name, number) VALUES "
        private const val UPDATE_BY_ID = "UPDATE driver SET name = '%s', number = %s WHERE driver_id = %d"
        private const val DELETE_BY_ID = "DELETE FROM driver WHERE driver_id = "

        private fun mapRowToDriver(rs: ResultSet): Driver = Driver(
            driverId = rs.getInt("driver_id"),
            name = rs.getString("name"),
            number = rs.getObject("number") as? Int
        )
    }

    fun findAll(): List<Driver> = queryForList(SELECT_ALL) { mapRowToDriver(it) }

    fun findById(id: Int): Driver =
        queryForObject("$SELECT_BY_ID$id") { mapRowToDriver(it) }
            ?: throw RepositoryException.NotFoundException("Driver with id $id not found")

    fun create(name: String, number: Int?): Int {
        val escapedName = name.replace("'", "\\'")
        val numberValue = if (number != null) "$number" else "NULL"
        val sql = "$INSERT ('$escapedName', $numberValue)"

        return withConnection { conn ->
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.executeUpdate()
                stmt.generatedKeys.use { rs ->
                    if (rs.next()) rs.getInt(1) else throw RepositoryException.DataAccessException("Failed to retrieve generated id")
                }
            }
        }
    }

    fun update(id: Int, name: String, number: Int?): Boolean {
        val escapedName = name.replace("'", "\\'")
        val numberValue = if (number != null) "$number" else "NULL"
        val sql = String.format(UPDATE_BY_ID, escapedName, numberValue, id)
        return update(sql) > 0
    }

    fun delete(id: Int): Boolean = update("$DELETE_BY_ID$id") > 0
}

