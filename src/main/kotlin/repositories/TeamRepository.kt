package com.racer.repositories

import com.racer.models.Team
import com.racer.database.DatabaseFactory
import javax.sql.DataSource
import java.sql.ResultSet

/**
 * Repository for Team entities.
 */
class TeamRepository : BaseRepository {
    override val dataSource: DataSource
        get() = DatabaseFactory.getDataSource()

    companion object {
        private const val SELECT_ALL = "SELECT team_id, name, driver_id, co_driver_id, car_id, manufacturer_id FROM team"
        private const val SELECT_BY_ID = "SELECT team_id, name, driver_id, co_driver_id, car_id, manufacturer_id FROM team WHERE team_id = "
        private const val INSERT = "INSERT INTO team (name, driver_id, co_driver_id, car_id, manufacturer_id) VALUES "
        private const val UPDATE_BY_ID = "UPDATE team SET name = '%s', driver_id = %s, co_driver_id = %s, car_id = %s, manufacturer_id = %s WHERE team_id = %d"
        private const val DELETE_BY_ID = "DELETE FROM team WHERE team_id = "

        private fun mapRowToTeam(rs: ResultSet): Team = Team(
            teamId = rs.getInt("team_id"),
            name = rs.getString("name"),
            driverId = rs.getObject("driver_id") as? Int,
            coDriverId = rs.getObject("co_driver_id") as? Int,
            carId = rs.getObject("car_id") as? Int,
            manufacturerId = rs.getObject("manufacturer_id") as? Int
        )
    }

    fun findAll(): List<Team> = queryForList(SELECT_ALL) { mapRowToTeam(it) }

    fun findById(id: Int): Team =
        queryForObject("$SELECT_BY_ID$id") { mapRowToTeam(it) }
            ?: throw RepositoryException.NotFoundException("Team with id $id not found")

    fun create(name: String, driverId: Int?, coDriverId: Int?, carId: Int?, manufacturerId: Int?): Int {
        val escapedName = name.replace("'", "\\'")
        val driverIdVal = driverId?.toString() ?: "NULL"
        val coDriverIdVal = coDriverId?.toString() ?: "NULL"
        val carIdVal = carId?.toString() ?: "NULL"
        val manufacturerIdVal = manufacturerId?.toString() ?: "NULL"
        val sql = "$INSERT ('$escapedName', $driverIdVal, $coDriverIdVal, $carIdVal, $manufacturerIdVal)"

        return withConnection { conn ->
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.executeUpdate()
                stmt.generatedKeys.use { rs ->
                    if (rs.next()) rs.getInt(1) else throw RepositoryException.DataAccessException("Failed to retrieve generated id")
                }
            }
        }
    }

    fun update(id: Int, name: String, driverId: Int?, coDriverId: Int?, carId: Int?, manufacturerId: Int?): Boolean {
        val escapedName = name.replace("'", "\\'")
        val driverIdVal = driverId?.toString() ?: "NULL"
        val coDriverIdVal = coDriverId?.toString() ?: "NULL"
        val carIdVal = carId?.toString() ?: "NULL"
        val manufacturerIdVal = manufacturerId?.toString() ?: "NULL"
        val sql = String.format(UPDATE_BY_ID, escapedName, driverIdVal, coDriverIdVal, carIdVal, manufacturerIdVal, id)
        return update(sql) > 0
    }

    fun delete(id: Int): Boolean = update("$DELETE_BY_ID$id") > 0
}

