package com.racing.db

import com.racing.data.Driver
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("DriverRepository")

class DriverRepository {

    fun getAll() : List<Driver> {
        val sql = "SELECT driver_id, name, number FROM driver ORDER BY driver_id;"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { resultSet ->
                    val rallies = mutableListOf<Driver>()
                    while (resultSet.next()) {
                        rallies += mapDriver(resultSet)
                    }
                    logger.debug("Retrieved ${rallies.size} rallies from database")
                    return rallies
                }
            }
        }
    }

    fun getById(id: Int): Driver? {
        val sql = "SELECT driver_id, name, number FROM driver WHERE driver_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        val driver = mapDriver(resultSet)
                        logger.debug("Retrieved driver with ID $id: ${driver.name}")
                        driver
                    } else {
                        logger.debug("Driver with ID $id not found")
                        null
                    }
                }
            }
        }
    }

    fun create(driver: Driver): Int {
        val sql = "INSERT INTO driver (name, number) VALUES (?, ?)"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql, RETURN_GENERATED_KEYS).use { statement ->
                statement.setString(1, driver.name)
                statement.setObject(2, driver.number)
                val rows = statement.executeUpdate()
                if (rows == 0) {
                    logger.debug("Failed creating new driver: ${driver.name}")
                    throw SQLException("Insert failed, no rows created")
                }

                statement.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val newId = keys.getInt(1)
                        logger.info("Created new driver: ID=$newId, name=${driver.name}")
                        return newId
                    } else {
                        logger.debug("Insert succeeded but no generated keys found for driver: ${driver.name}")
                        throw SQLException("Insert succeeded but no generated keys found")
                    }

                }
            }
        }
    }

    fun update(id: Int, driver: Driver): Int {
        val sql = "UPDATE driver SET name = ?, number = ? WHERE driver_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, driver.name)
                statement.setObject(2, driver.number)
                statement.setInt(3, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Updated driver with ID $id: ${driver.name}")
                } else {
                    logger.debug("Update for driver ID $id had no effect (driver not found)")
                }
                return rows
            }
        }
    }

    fun delete(id: Int): Int {
        val sql = "DELETE FROM driver WHERE driver_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Deleted driver with ID $id")
                } else {
                    logger.debug("Delete for driver ID $id had no effect (driver not found)")
                }
                return rows
            }
        }
    }
    private fun mapDriver(rs: ResultSet) = Driver(
        driver_id = rs.getInt("driver_id"),
        name = rs.getString("name"),
        number = rs.getInt("number")
    )
}