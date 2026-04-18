package com.racing.db

import com.racing.data.Stage
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS

private val logger = LoggerFactory.getLogger("StageRepository")

class StageRepository {

    fun getAll() : List<Stage> {
        val sql = "SELECT stage_id, name, distance FROM stage ORDER BY stage_id;"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { resultSet ->
                    val stages = mutableListOf<Stage>()
                    while (resultSet.next()) {
                        stages += mapStage(resultSet)
                    }
                    logger.debug("Retrieved ${stages.size} stages from database")
                    return stages
                }
            }
        }
    }

    fun getById(id: Int): Stage? {
        val sql = "SELECT stage_id, name, distance FROM stage WHERE stage_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        val stage = mapStage(resultSet)
                        logger.debug("Retrieved stage with ID $id: ${stage.name}")
                        stage
                    } else {
                        logger.debug("Stage with ID $id not found")
                        null
                    }
                }
            }
        }
    }

    fun create(stage: Stage): Int {
        val sql = "INSERT INTO stage (name, distance) VALUES (?, ?)"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql, RETURN_GENERATED_KEYS).use { statement ->
                statement.setString(1, stage.name)
                statement.setObject(2, stage.distance)
                val rows = statement.executeUpdate()
                if (rows == 0) {
                    logger.debug("Failed creating new stage: ${stage.name}")
                    throw SQLException("Insert failed, no rows created")
                }

                statement.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val newId = keys.getInt(1)
                        logger.info("Created new stage: ID=$newId, name=${stage.name}")
                        return newId
                    } else {
                        logger.debug("Insert succeeded but no generated keys found for stage: ${stage.name}")
                        throw SQLException("Insert succeeded but no generated keys found")
                    }

                }
            }
        }
    }

    fun update(id: Int, stage: Stage): Int {
        val sql = "UPDATE stage SET name = ?, distance = ? WHERE stage_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, stage.name)
                statement.setObject(2, stage.distance)
                statement.setInt(3, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Updated stage with ID $id: ${stage.name}")
                } else {
                    logger.debug("Update for stage ID $id had no effect (stage not found)")
                }
                return rows
            }
        }
    }

    fun delete(id: Int): Int {
        val sql = "DELETE FROM stage WHERE stage_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Deleted stage with ID $id")
                } else {
                    logger.debug("Delete for stage ID $id had no effect (stage not found)")
                }
                return rows
            }
        }
    }
    private fun mapStage(rs: ResultSet) = Stage(
        stage_id = rs.getInt("stage_id"),
        name = rs.getString("name"),
        distance = rs.getDouble("distance")
    )
}