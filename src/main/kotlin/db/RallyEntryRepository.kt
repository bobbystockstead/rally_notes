package com.racing.db

import com.racing.data.RallyEntry
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS

private val logger = LoggerFactory.getLogger("RallyEntryRepository")

class RallyEntryRepository {

    fun getAll() : List<RallyEntry> {
        val sql = "SELECT entry_id, rally_id, crew_id, car_number FROM rally_entry ORDER BY entry_id;"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { resultSet ->
                    val rallyEntries = mutableListOf<RallyEntry>()
                    while (resultSet.next()) {
                        rallyEntries += mapRallyEntry(resultSet)
                    }
                    logger.debug("Retrieved ${rallyEntries.size} rallyEntries from database")
                    return rallyEntries
                }
            }
        }
    }

    fun getById(id: Int): RallyEntry? {
        val sql = "SELECT entry_id, rally_id, crew_id, car_number FROM rally_entry WHERE entry_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        val rallyEntry = mapRallyEntry(resultSet)
                        logger.debug("Retrieved rallyEntry with ID $id: ${rallyEntry.entry_id}")
                        rallyEntry
                    } else {
                        logger.debug("RallyEntry with ID $id not found")
                        null
                    }
                }
            }
        }
    }

    fun create(rallyEntry: RallyEntry): Int {
        val sql = "INSERT INTO rally_entry (rally_id, crew_id, car_number) VALUES (?, ?, ?)"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql, RETURN_GENERATED_KEYS).use { statement ->
                statement.setObject(1, rallyEntry.rally_id)
                statement.setObject(2, rallyEntry.crew_id)
                statement.setObject(3, rallyEntry.car_number)

                val rows = statement.executeUpdate()
                if (rows == 0) {
                    logger.debug("Failed creating new rally_id: ${rallyEntry.rally_id} and crew_id: ${rallyEntry.crew_id}")
                    throw SQLException("Insert failed, no rows created")
                }

                statement.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val newId = keys.getInt(1)
                        logger.info("Created new rallyEntry: ID=$newId")
                        return newId
                    } else {
                        logger.debug("Insert succeeded but no generated keys found for rallyEntry: ${rallyEntry.entry_id}")
                        throw SQLException("Insert succeeded but no generated keys found")
                    }
                }
            }
        }
    }

    fun update(id: Int, rallyEntry: RallyEntry): Int {
        val sql = "UPDATE rally_entry SET rally_id = ?, crew_id = ?, car_number = ? WHERE entry_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setObject(1, rallyEntry.rally_id)
                statement.setObject(2, rallyEntry.crew_id)
                statement.setObject(3, rallyEntry.car_number)
                statement.setInt(4, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Updated rallyEntry with ID $id: ${rallyEntry.entry_id}")
                } else {
                    logger.debug("Update for rallyEntry ID $id had no effect (rallyEntry not found)")
                }
                return rows
            }
        }
    }

    fun delete(id: Int): Int {
        val sql = "DELETE FROM rally_entry WHERE entry_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Deleted rallyEntry with ID $id")
                } else {
                    logger.debug("Delete for rallyEntry ID $id had no effect (rallyEntry not found)")
                }
                return rows
            }
        }
    }
    private fun mapRallyEntry(rs: ResultSet) = RallyEntry(
        entry_id = rs.getInt("entry_id"),
        rally_id = rs.getObject("rally_id") as? Int,
        crew_id = rs.getObject("crew_id") as? Int,
        car_number = rs.getObject("car_number") as? Int
    )
}