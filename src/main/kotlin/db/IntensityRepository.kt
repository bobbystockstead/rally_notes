package com.racing.db

import com.racing.data.Intensity
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS

private val logger = LoggerFactory.getLogger("IntensityRepository")

class IntensityRepository {

    fun getAll() : List<Intensity> {
        val sql = "SELECT intensity_id, name FROM intensity ORDER BY intensity_id;"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { resultSet ->
                    val intensities = mutableListOf<Intensity>()
                    while (resultSet.next()) {
                        intensities += mapIntensity(resultSet)
                    }
                    logger.debug("Retrieved ${intensities.size} intensities from database")
                    return intensities
                }
            }
        }
    }

    fun getById(id: Int): Intensity? {
        val sql = "SELECT intensity_id, name FROM intensity WHERE intensity_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        val intensity = mapIntensity(resultSet)
                        logger.debug("Retrieved intensity with ID $id: ${intensity.name}")
                        intensity
                    } else {
                        logger.debug("Intensity with ID $id not found")
                        null
                    }
                }
            }
        }
    }

    fun create(intensity: Intensity): Int {
        val sql = "INSERT INTO intensity (name) VALUES (?)"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql, RETURN_GENERATED_KEYS).use { statement ->
                statement.setString(1, intensity.name)
                val rows = statement.executeUpdate()
                if (rows == 0) {
                    logger.debug("Failed creating new intensity: ${intensity.name}")
                    throw SQLException("Insert failed, no rows created")
                }

                statement.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val newId = keys.getInt(1)
                        logger.info("Created new intensity: ID=$newId, name=${intensity.name}")
                        return newId
                    } else {
                        logger.debug("Insert succeeded but no generated keys found for intensity: ${intensity.name}")
                        throw SQLException("Insert succeeded but no generated keys found")
                    }

                }
            }
        }
    }

    fun update(id: Int, intensity: Intensity): Int {
        val sql = "UPDATE intensity SET name = ? WHERE intensity_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, intensity.name)
                statement.setInt(2, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Updated intensity with ID $id: ${intensity.name}")
                } else {
                    logger.debug("Update for intensity ID $id had no effect (intensity not found)")
                }
                return rows
            }
        }
    }

    fun delete(id: Int): Int {
        val sql = "DELETE FROM intensity WHERE intensity_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Deleted intensity with ID $id")
                } else {
                    logger.debug("Delete for intensity ID $id had no effect (intensity not found)")
                }
                return rows
            }
        }
    }
    private fun mapIntensity(rs: ResultSet) = Intensity(
        intensity_id = rs.getInt("intensity_id"),
        name = rs.getString("name")
    )
}