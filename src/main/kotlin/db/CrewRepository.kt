package com.racing.db

import com.racing.data.Crew
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS

private val logger = LoggerFactory.getLogger("CrewRepository")

class CrewRepository {

    fun getAll() : List<Crew> {
        val sql = "SELECT crew_id, driver_id, codriver_id, car_id, team_id FROM crew ORDER BY crew_id;"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { resultSet ->
                    val crews = mutableListOf<Crew>()
                    while (resultSet.next()) {
                        crews += mapCrew(resultSet)
                    }
                    logger.debug("Retrieved ${crews.size} crews from database")
                    return crews
                }
            }
        }
    }

    fun getById(id: Int): Crew? {
        val sql = "SELECT crew_id, driver_id, codriver_id, car_id, team_id FROM crew WHERE crew_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        val crew = mapCrew(resultSet)
                        logger.debug("Retrieved crew with ID $id: ${crew.crew_id}")
                        crew
                    } else {
                        logger.debug("Crew with ID $id not found")
                        null
                    }
                }
            }
        }
    }

    fun create(crew: Crew): Int {
        val sql = "INSERT INTO crew (driver_id, codriver_id, car_id, team_id) VALUES (?, ?, ?, ?)"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql, RETURN_GENERATED_KEYS).use { statement ->
                statement.setObject(1, crew.driver_id)
                statement.setObject(2, crew.codriver_id)
                statement.setObject(3, crew.car_id)
                statement.setObject(4, crew.team_id)
                val rows = statement.executeUpdate()
                if (rows == 0) {
                    logger.debug("Failed creating new crew: ${crew.driver_id}")
                    throw SQLException("Insert failed, no rows created")
                }

                statement.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val newId = keys.getInt(1)
                        logger.info("Created new crew: ID=$newId, driverid=${crew.driver_id}")
                        return newId
                    } else {
                        logger.debug("Insert succeeded but no generated keys found for crew: ${crew.crew_id}")
                        throw SQLException("Insert succeeded but no generated keys found")
                    }
                }
            }
        }
    }

    fun update(id: Int, crew: Crew): Int {
        val sql = "UPDATE crew SET driver_id = ?, codriver_id = ?, car_id = ?, team_id = ? WHERE crew_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setObject(1, crew.driver_id)
                statement.setObject(2, crew.codriver_id)
                statement.setObject(3, crew.car_id)
                statement.setObject(4, crew.team_id)
                statement.setInt(5, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Updated crew with ID $id: and Driver ID ${crew.driver_id}")
                } else {
                    logger.debug("Update for crew ID $id had no effect (crew not found)")
                }
                return rows
            }
        }
    }

    fun delete(id: Int): Int {
        val sql = "DELETE FROM crew WHERE crew_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Deleted crew with ID $id")
                } else {
                    logger.debug("Delete for crew ID $id had no effect (crew not found)")
                }
                return rows
            }
        }
    }
    private fun mapCrew(rs: ResultSet) = Crew(
        crew_id = rs.getInt("crew_id"),
        driver_id = rs.getObject("driver_id") as? Int,
        codriver_id = rs.getObject("codriver_id") as? Int,
        car_id = rs.getObject("car_id") as? Int,
        team_id = rs.getObject("team_id") as? Int
    )
}