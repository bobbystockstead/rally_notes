package com.racing.db

import com.racing.data.Codriver
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("CodriverRepository")

class CodriverRepository {

    fun getAll() : List<Codriver> {
        val sql = "SELECT codriver_id, name, number FROM codriver ORDER BY codriver_id;"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { resultSet ->
                    val rallies = mutableListOf<Codriver>()
                    while (resultSet.next()) {
                        rallies += mapCodriver(resultSet)
                    }
                    logger.debug("Retrieved ${rallies.size} rallies from database")
                    return rallies
                }
            }
        }
    }

    fun getById(id: Int): Codriver? {
        val sql = "SELECT codriver_id, name, number FROM codriver WHERE codriver_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        val codriver = mapCodriver(resultSet)
                        logger.debug("Retrieved codriver with ID $id: ${codriver.name}")
                        codriver
                    } else {
                        logger.debug("Codriver with ID $id not found")
                        null
                    }
                }
            }
        }
    }

    fun create(codriver: Codriver): Int {
        val sql = "INSERT INTO codriver (name, number) VALUES (?, ?)"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql, RETURN_GENERATED_KEYS).use { statement ->
                statement.setString(1, codriver.name)
                statement.setObject(2, codriver.number)
                val rows = statement.executeUpdate()
                if (rows == 0) {
                    logger.debug("Failed creating new codriver: ${codriver.name}")
                    throw SQLException("Insert failed, no rows created")
                }

                statement.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val newId = keys.getInt(1)
                        logger.info("Created new codriver: ID=$newId, name=${codriver.name}")
                        return newId
                    } else {
                        logger.debug("Insert succeeded but no generated keys found for codriver: ${codriver.name}")
                        throw SQLException("Insert succeeded but no generated keys found")
                    }

                }
            }
        }
    }

    fun update(id: Int, codriver: Codriver): Int {
        val sql = "UPDATE codriver SET name = ?, number = ? WHERE codriver_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, codriver.name)
                statement.setObject(2, codriver.number)
                statement.setInt(3, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Updated codriver with ID $id: ${codriver.name}")
                } else {
                    logger.debug("Update for codriver ID $id had no effect (codriver not found)")
                }
                return rows
            }
        }
    }

    fun delete(id: Int): Int {
        val sql = "DELETE FROM codriver WHERE codriver_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Deleted codriver with ID $id")
                } else {
                    logger.debug("Delete for codriver ID $id had no effect (codriver not found)")
                }
                return rows
            }
        }
    }
    private fun mapCodriver(rs: ResultSet) = Codriver(
        codriver_id = rs.getInt("codriver_id"),
        name = rs.getString("name"),
        number = rs.getInt("number")
    )
}