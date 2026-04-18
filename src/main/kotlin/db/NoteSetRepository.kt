package com.racing.db

import com.racing.data.NoteSet
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS

private val logger = LoggerFactory.getLogger("NoteSetRepository")

class NoteSetRepository {

    fun getAll() : List<NoteSet> {
        val sql = "SELECT note_id, crew_id, name, stage_id, conditions FROM note_set ORDER BY note_id;"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { resultSet ->
                    val rallyEntries = mutableListOf<NoteSet>()
                    while (resultSet.next()) {
                        rallyEntries += mapNoteSet(resultSet)
                    }
                    logger.debug("Retrieved ${rallyEntries.size} rallyEntries from database")
                    return rallyEntries
                }
            }
        }
    }

    fun getById(id: Int): NoteSet? {
        val sql = "SELECT note_id, crew_id, name, stage_id, conditions FROM note_set WHERE note_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        val noteSet = mapNoteSet(resultSet)
                        logger.debug("Retrieved noteSet with ID $id: ${noteSet.note_id}")
                        noteSet
                    } else {
                        logger.debug("NoteSet with ID $id not found")
                        null
                    }
                }
            }
        }
    }

    fun create(noteSet: NoteSet): Int {
        val sql = "INSERT INTO note_set (crew_id, name, stage_id, conditions) VALUES (?, ?, ?, ?)"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql, RETURN_GENERATED_KEYS).use { statement ->
                statement.setObject(1, noteSet.crew_id)
                statement.setString(2, noteSet.name)
                statement.setObject(3, noteSet.stage_id)
                statement.setString(4, noteSet.conditions)

                val rows = statement.executeUpdate()
                if (rows == 0) {
                    logger.debug("Failed creating new for crew_id: ${noteSet.crew_id} and stage_id: ${noteSet.stage_id}")
                    throw SQLException("Insert failed, no rows created")
                }

                statement.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val newId = keys.getInt(1)
                        logger.info("Created new noteSet: ID=$newId")
                        return newId
                    } else {
                        logger.debug("Insert succeeded but no generated keys found for noteSet: ${noteSet.note_id}")
                        throw SQLException("Insert succeeded but no generated keys found")
                    }
                }
            }
        }
    }

    fun update(id: Int, noteSet: NoteSet): Int {
        val sql = "UPDATE note_set SET crew_id = ?, name = ?, stage_id = ?, conditions = ? WHERE note_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setObject(1, noteSet.crew_id)
                statement.setString(2, noteSet.name)
                statement.setObject(3, noteSet.stage_id)
                statement.setString(4, noteSet.conditions)
                statement.setInt(5, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Updated noteSet with ID $id: ${noteSet.note_id}")
                } else {
                    logger.debug("Update for noteSet ID $id had no effect (noteSet not found)")
                }
                return rows
            }
        }
    }

    fun delete(id: Int): Int {
        val sql = "DELETE FROM note_set WHERE note_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Deleted noteSet with ID $id")
                } else {
                    logger.debug("Delete for noteSet ID $id had no effect (noteSet not found)")
                }
                return rows
            }
        }
    }
    private fun mapNoteSet(rs: ResultSet) = NoteSet(
        note_id = rs.getInt("note_id"),
        crew_id = rs.getObject("crew_id") as? Int,
        name = rs.getString("name"),
        stage_id = rs.getObject("stage_id") as? Int,
        conditions = rs.getString("conditions")
    )
}