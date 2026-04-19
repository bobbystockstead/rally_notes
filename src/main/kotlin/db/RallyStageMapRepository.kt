package com.racing.db

import com.racing.data.RallyStageMap
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS

private val logger = LoggerFactory.getLogger("RallyStageMapRepository")

class RallyStageMapRepository {

    fun getAll() : List<RallyStageMap> {
        val sql = "SELECT rally_stage_id, rally_id, stage_id, stage_order FROM rally_stage_map ORDER BY rally_stage_id;"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { resultSet ->
                    val rallyStageMaps = mutableListOf<RallyStageMap>()
                    while (resultSet.next()) {
                        rallyStageMaps += mapRallyStageMap(resultSet)
                    }
                    logger.debug("Retrieved ${rallyStageMaps.size} rallyStageMaps from database")
                    return rallyStageMaps
                }
            }
        }
    }

    fun getById(id: Int): RallyStageMap? {
        val sql = "SELECT rally_stage_id, rally_id, stage_id, stage_order FROM rally_stage_map WHERE rally_stage_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        val rallyStageMap = mapRallyStageMap(resultSet)
                        logger.debug("Retrieved rallyStageMap with ID $id: ${rallyStageMap.rally_stage_id}")
                        rallyStageMap
                    } else {
                        logger.debug("RallyStageMap with ID $id not found")
                        null
                    }
                }
            }
        }
    }

    fun create(rallyStageMap: RallyStageMap): Int {
        val sql = "INSERT INTO rally_stage_map (rally_id, stage_id, stage_order) VALUES (?, ?, ?)"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql, RETURN_GENERATED_KEYS).use { statement ->
                statement.setObject(1, rallyStageMap.rally_id)
                statement.setObject(2, rallyStageMap.stage_id)
                statement.setObject(3, rallyStageMap.stage_order)

                val rows = statement.executeUpdate()
                if (rows == 0) {
                    logger.debug("Failed creating new rally_id: ${rallyStageMap.rally_id} and stage_id: ${rallyStageMap.stage_id}")
                    throw SQLException("Insert failed, no rows created")
                }

                statement.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val newId = keys.getInt(1)
                        logger.info("Created new rallyStageMap: ID=$newId")
                        return newId
                    } else {
                        logger.debug("Insert succeeded but no generated keys found for rallyStageMap: ${rallyStageMap.rally_stage_id}")
                        throw SQLException("Insert succeeded but no generated keys found")
                    }
                }
            }
        }
    }

    fun update(id: Int, rallyStageMap: RallyStageMap): Int {
        val sql = "UPDATE rally_stage_map SET rally_id = ?, stage_id = ?, stage_order = ? WHERE rally_stage_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setObject(1, rallyStageMap.rally_id)
                statement.setObject(2, rallyStageMap.stage_id)
                statement.setObject(3, rallyStageMap.stage_order)
                statement.setInt(4, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Updated rallyStageMap with ID $id: ${rallyStageMap.rally_stage_id}")
                } else {
                    logger.debug("Update for rallyStageMap ID $id had no effect (rallyStageMap not found)")
                }
                return rows
            }
        }
    }

    fun delete(id: Int): Int {
        val sql = "DELETE FROM rally_stage_map WHERE rally_stage_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Deleted rallyStageMap with ID $id")
                } else {
                    logger.debug("Delete for rallyStageMap ID $id had no effect (rallyStageMap not found)")
                }
                return rows
            }
        }
    }
    private fun mapRallyStageMap(rs: ResultSet) = RallyStageMap(
        rally_stage_id = rs.getInt("rally_stage_id"),
        rally_id = rs.getObject("rally_id") as? Int,
        stage_id = rs.getObject("stage_id") as? Int,
        stage_order = rs.getObject("stage_order") as? Int
    )
}