package com.racing.db

import com.racing.data.Tip
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS

private val logger = LoggerFactory.getLogger("TipRepository")

class TipRepository {

    fun getAll() : List<Tip> {
        val sql = "SELECT tip_id, description FROM tip ORDER BY tip_id;"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { resultSet ->
                    val tips = mutableListOf<Tip>()
                    while (resultSet.next()) {
                        tips += mapTip(resultSet)
                    }
                    logger.debug("Retrieved ${tips.size} tips from database")
                    return tips
                }
            }
        }
    }

    fun getById(id: Int): Tip? {
        val sql = "SELECT tip_id, description FROM tip WHERE tip_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        val tip = mapTip(resultSet)
                        logger.debug("Retrieved tip with ID $id: ${tip.description}")
                        tip
                    } else {
                        logger.debug("Tip with ID $id not found")
                        null
                    }
                }
            }
        }
    }

    fun create(tip: Tip): Int {
        val sql = "INSERT INTO tip (description) VALUES (?)"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql, RETURN_GENERATED_KEYS).use { statement ->
                statement.setString(1, tip.description)
                val rows = statement.executeUpdate()
                if (rows == 0) {
                    logger.debug("Failed creating new tip: ${tip.description}")
                    throw SQLException("Insert failed, no rows created")
                }

                statement.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val newId = keys.getInt(1)
                        logger.info("Created new tip: ID=$newId, description=${tip.description}")
                        return newId
                    } else {
                        logger.debug("Insert succeeded but no generated keys found for tip: ${tip.description}")
                        throw SQLException("Insert succeeded but no generated keys found")
                    }

                }
            }
        }
    }

    fun update(id: Int, tip: Tip): Int {
        val sql = "UPDATE tip SET description = ? WHERE tip_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, tip.description)
                statement.setInt(2, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Updated tip with ID $id: ${tip.description}")
                } else {
                    logger.debug("Update for tip ID $id had no effect (tip not found)")
                }
                return rows
            }
        }
    }

    fun delete(id: Int): Int {
        val sql = "DELETE FROM tip WHERE tip_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Deleted tip with ID $id")
                } else {
                    logger.debug("Delete for tip ID $id had no effect (tip not found)")
                }
                return rows
            }
        }
    }
    private fun mapTip(rs: ResultSet) = Tip(
        tip_id = rs.getInt("tip_id"),
        description = rs.getString("description")
    )
}