package com.racing.db

import com.racing.data.Rally
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS
import java.time.LocalDate
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("RallyRepository")

class RallyRepository {

    fun getAll() : List<Rally> {
        val sql = "SELECT rally_id, name, date FROM rally ORDER BY rally_id;"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { resultSet ->
                    val rallies = mutableListOf<Rally>()
                    while (resultSet.next()) {
                        rallies += mapRally(resultSet)
                    }
                    logger.debug("Retrieved ${rallies.size} rallies from database")
                    return rallies
                }
            }
        }
    }

    fun getById(id: Int): Rally? {
        val sql = "SELECT rally_id, name, date FROM rally WHERE rally_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        val rally = mapRally(resultSet)
                        logger.debug("Retrieved rally with ID $id: ${rally.name}")
                        rally
                    } else {
                        logger.debug("Rally with ID $id not found")
                        null
                    }
                }
            }
        }
    }

    fun create(rally: Rally): Int {
        val sql = "INSERT INTO rally (name, date) VALUES (?, ?)"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql, RETURN_GENERATED_KEYS).use { statement ->
                statement.setString(1, rally.name)
                statement.setObject(2, rally.date.toJavaLocalDate()) // convert to JDBC DATE compatible object
                val rows = statement.executeUpdate()
                if (rows == 0) {
                    logger.debug("Failed creating new rally: ${rally.name}")
                    throw SQLException("Insert failed, no rows created")
                }

                statement.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val newId = keys.getInt(1)
                        logger.info("Created new rally: ID=$newId, name=${rally.name}")
                        return newId
                    } else {
                        logger.debug("Insert succeeded but no generated keys found for rally: ${rally.name}")
                        throw SQLException("Insert succeeded but no generated keys found")
                    }

                }
            }
        }
    }

    fun update(id: Int, rally: Rally): Int {
        val sql = "UPDATE rally SET name = ?, date = ? WHERE rally_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, rally.name)
                statement.setObject(2, rally.date.toJavaLocalDate())
                statement.setInt(3, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Updated rally with ID $id: ${rally.name}")
                } else {
                    logger.debug("Update for rally ID $id had no effect (rally not found)")
                }
                return rows
            }
        }
    }

    fun delete(id: Int): Int {
        val sql = "DELETE FROM rally WHERE rally_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Deleted rally with ID $id")
                } else {
                    logger.debug("Delete for rally ID $id had no effect (rally not found)")
                }
                return rows
            }
        }
    }
    private fun mapRally(rs: ResultSet) = Rally(
        rally_id = rs.getInt("rally_id"),
        name = rs.getString("name"),
        date = rs.getObject("date", LocalDate::class.java).toKotlinLocalDate()
    )
}