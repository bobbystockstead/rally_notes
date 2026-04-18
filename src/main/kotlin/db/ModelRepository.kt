package com.racing.db

import com.racing.data.Model
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS

private val logger = LoggerFactory.getLogger("ModelRepository")

class ModelRepository {

    fun getAll() : List<Model> {
        val sql = "SELECT model_id, name, manufacturer_id FROM model ORDER BY model_id;"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { resultSet ->
                    val models = mutableListOf<Model>()
                    while (resultSet.next()) {
                        models += mapModel(resultSet)
                    }
                    logger.debug("Retrieved ${models.size} models from database")
                    return models
                }
            }
        }
    }

    fun getById(id: Int): Model? {
        val sql = "SELECT model_id, name, manufacturer_id FROM model WHERE model_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        val model = mapModel(resultSet)
                        logger.debug("Retrieved model with ID $id: ${model.name}")
                        model
                    } else {
                        logger.debug("Model with ID $id not found")
                        null
                    }
                }
            }
        }
    }

    fun create(model: Model): Int {
        val sql = "INSERT INTO model (name, manufacturer_id) VALUES (?, ?)"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql, RETURN_GENERATED_KEYS).use { statement ->
                statement.setString(1, model.name)
                statement.setObject(2, model.manufacturer_id)
                val rows = statement.executeUpdate()
                if (rows == 0) {
                    logger.debug("Failed creating new model: ${model.name}")
                    throw SQLException("Insert failed, no rows created")
                }

                statement.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val newId = keys.getInt(1)
                        logger.info("Created new model: ID=$newId, name=${model.name}")
                        return newId
                    } else {
                        logger.debug("Insert succeeded but no generated keys found for model: ${model.name}")
                        throw SQLException("Insert succeeded but no generated keys found")
                    }
                }
            }
        }
    }

    fun update(id: Int, model: Model): Int {
        val sql = "UPDATE model SET name = ?, manufacturer_id = ? WHERE model_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, model.name)
                statement.setObject(2, model.manufacturer_id)
                statement.setInt(3, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Updated model with ID $id: ${model.name}")
                } else {
                    logger.debug("Update for model ID $id had no effect (model not found)")
                }
                return rows
            }
        }
    }

    fun delete(id: Int): Int {
        val sql = "DELETE FROM model WHERE model_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Deleted model with ID $id")
                } else {
                    logger.debug("Delete for model ID $id had no effect (model not found)")
                }
                return rows
            }
        }
    }
    private fun mapModel(rs: ResultSet) = Model(
        model_id = rs.getInt("model_id"),
        name = rs.getString("name"),
        manufacturer_id = rs.getObject("manufacturer_id") as? Int
    )
}