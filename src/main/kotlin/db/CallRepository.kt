package com.racing.db

import com.racing.data.Call
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS

private val logger = LoggerFactory.getLogger("CallRepository")

class CallRepository {

    fun getAll() : List<Call> {
        val sql = "SELECT call_id, note_id, sequence_number, gear, direction, distance, intensity_id, warning_id, tip_id FROM `call` ORDER BY call_id;"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { resultSet ->
                    val calls = mutableListOf<Call>()
                    while (resultSet.next()) {
                        calls += mapCall(resultSet)
                    }
                    logger.debug("Retrieved ${calls.size} calls from database")
                    return calls
                }
            }
        }
    }

    fun getById(id: Int): Call? {
        val sql = "SELECT call_id, note_id, sequence_number, gear, direction, distance, intensity_id, warning_id, tip_id FROM `call` WHERE call_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        val call = mapCall(resultSet)
                        logger.debug("Retrieved call with ID $id: ${call.call_id}")
                        call
                    } else {
                        logger.debug("Call with ID $id not found")
                        null
                    }
                }
            }
        }
    }

    fun create(call: Call): Int {
        val sql = "INSERT INTO `call` (note_id, sequence_number, gear, direction, distance, intensity_id, warning_id, tip_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql, RETURN_GENERATED_KEYS).use { statement ->
                statement.setObject(1, call.note_id)
                statement.setObject(2, call.sequence_number)
                statement.setObject(3, call.gear)
                statement.setString(4, call.direction)
                statement.setObject(5, call.distance)
                statement.setObject(6, call.intensity_id)
                statement.setObject(7, call.warning_id)
                statement.setObject(8, call.tip_id)

                val rows = statement.executeUpdate()
                if (rows == 0) {
                    logger.debug("Failed creating new call: ${call.note_id} and sequence number: ${call.sequence_number}")
                    throw SQLException("Insert failed, no rows created")
                }

                statement.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val newId = keys.getInt(1)
                        logger.info("Created new call: ID=$newId, note_id =${call.note_id}")
                        return newId
                    } else {
                        logger.debug("Insert succeeded but no generated keys found for call: ${call.call_id}")
                        throw SQLException("Insert succeeded but no generated keys found")
                    }
                }
            }
        }
    }

    fun update(id: Int, call: Call): Int {
        val sql = "UPDATE `call` SET note_id = ?, sequence_number = ?, gear = ?, direction = ?, distance = ?, intensity_id = ?, warning_id= ?, tip_id = ? WHERE call_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setObject(1, call.note_id)
                statement.setObject(2, call.sequence_number)
                statement.setObject(3, call.gear)
                statement.setString(4, call.direction)
                statement.setObject(5, call.distance)
                statement.setObject(6, call.intensity_id)
                statement.setObject(7, call.warning_id)
                statement.setObject(8, call.tip_id)
                statement.setInt(9, id)

                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Updated call with ID $id: and Note ID ${call.note_id} and sequence number: ${call.sequence_number}")
                } else {
                    logger.debug("Update for call ID $id had no effect (call not found)")
                }
                return rows
            }
        }
    }

    fun delete(id: Int): Int {
        val sql = "DELETE FROM `call` WHERE call_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Deleted call with ID $id")
                } else {
                    logger.debug("Delete for call ID $id had no effect (call not found)")
                }
                return rows
            }
        }
    }
    private fun mapCall(rs: ResultSet) = Call(
        call_id = rs.getInt("call_id"),
        note_id = rs.getObject("note_id") as? Int,
        sequence_number = rs.getObject("sequence_number") as? Int,
        gear = rs.getObject("gear") as? Int,
        direction = rs.getObject("direction") as? String,
        distance = rs.getObject("distance") as? Int,
        intensity_id = rs.getObject("intensity_id") as? Int,
        warning_id = rs.getObject("warning_id") as? Int,
        tip_id = rs.getObject("tip_id") as? Int
    )
}