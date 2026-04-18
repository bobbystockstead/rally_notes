package com.racing.db

import com.racing.data.Warning
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS

private val logger = LoggerFactory.getLogger("WarningRepository")

class WarningRepository {

    fun getAll() : List<Warning> {
        val sql = "SELECT warning_id, description FROM warning ORDER BY warning_id;"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { resultSet ->
                    val warnings = mutableListOf<Warning>()
                    while (resultSet.next()) {
                        warnings += mapWarning(resultSet)
                    }
                    logger.debug("Retrieved ${warnings.size} warnings from database")
                    return warnings
                }
            }
        }
    }

    fun getById(id: Int): Warning? {
        val sql = "SELECT warning_id, description FROM warning WHERE warning_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        val warning = mapWarning(resultSet)
                        logger.debug("Retrieved warning with ID $id: ${warning.description}")
                        warning
                    } else {
                        logger.debug("Warning with ID $id not found")
                        null
                    }
                }
            }
        }
    }

    fun create(warning: Warning): Int {
        val sql = "INSERT INTO warning (description) VALUES (?)"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql, RETURN_GENERATED_KEYS).use { statement ->
                statement.setString(1, warning.description)
                val rows = statement.executeUpdate()
                if (rows == 0) {
                    logger.debug("Failed creating new warning: ${warning.description}")
                    throw SQLException("Insert failed, no rows created")
                }

                statement.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val newId = keys.getInt(1)
                        logger.info("Created new warning: ID=$newId, description=${warning.description}")
                        return newId
                    } else {
                        logger.debug("Insert succeeded but no generated keys found for warning: ${warning.description}")
                        throw SQLException("Insert succeeded but no generated keys found")
                    }

                }
            }
        }
    }

    fun update(id: Int, warning: Warning): Int {
        val sql = "UPDATE warning SET description = ? WHERE warning_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, warning.description)
                statement.setInt(2, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Updated warning with ID $id: ${warning.description}")
                } else {
                    logger.debug("Update for warning ID $id had no effect (warning not found)")
                }
                return rows
            }
        }
    }

    fun delete(id: Int): Int {
        val sql = "DELETE FROM warning WHERE warning_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Deleted warning with ID $id")
                } else {
                    logger.debug("Delete for warning ID $id had no effect (warning not found)")
                }
                return rows
            }
        }
    }
    private fun mapWarning(rs: ResultSet) = Warning(
        warning_id = rs.getInt("warning_id"),
        description = rs.getString("description")
    )
}