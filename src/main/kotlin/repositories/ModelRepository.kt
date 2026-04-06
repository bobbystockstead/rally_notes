package com.racer.repositories

import com.racer.models.Model
import com.racer.database.DatabaseFactory
import javax.sql.DataSource
import java.sql.ResultSet

/**
 * Repository for Model entities.
 */
class ModelRepository : BaseRepository {
    override val dataSource: DataSource
        get() = DatabaseFactory.getDataSource()

    companion object {
        private const val SELECT_ALL = "SELECT model_id, name, manufacturer_id FROM model"
        private const val SELECT_BY_ID = "SELECT model_id, name, manufacturer_id FROM model WHERE model_id = "
        private const val SELECT_BY_MANUFACTURER = "SELECT model_id, name, manufacturer_id FROM model WHERE manufacturer_id = "
        private const val INSERT = "INSERT INTO model (name, manufacturer_id) VALUES "
        private const val UPDATE_BY_ID = "UPDATE model SET name = '%s', manufacturer_id = %d WHERE model_id = %d"
        private const val DELETE_BY_ID = "DELETE FROM model WHERE model_id = "

        private fun mapRowToModel(rs: ResultSet): Model = Model(
            modelId = rs.getInt("model_id"),
            name = rs.getString("name"),
            manufacturerId = rs.getInt("manufacturer_id")
        )
    }

    fun findAll(): List<Model> = queryForList(SELECT_ALL) { mapRowToModel(it) }

    fun findById(id: Int): Model =
        queryForObject("$SELECT_BY_ID$id") { mapRowToModel(it) }
            ?: throw RepositoryException.NotFoundException("Model with id $id not found")

    fun findByManufacturer(manufacturerId: Int): List<Model> =
        queryForList("$SELECT_BY_MANUFACTURER$manufacturerId") { mapRowToModel(it) }

    fun create(name: String, manufacturerId: Int): Int {
        val escapedName = name.replace("'", "\\'")
        val sql = "$INSERT ('$escapedName', $manufacturerId)"

        return withConnection { conn ->
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.executeUpdate()
                stmt.generatedKeys.use { rs ->
                    if (rs.next()) rs.getInt(1) else throw RepositoryException.DataAccessException("Failed to retrieve generated id")
                }
            }
        }
    }

    fun update(id: Int, name: String, manufacturerId: Int): Boolean {
        val escapedName = name.replace("'", "\\'")
        val sql = String.format(UPDATE_BY_ID, escapedName, manufacturerId, id)
        return update(sql) > 0
    }

    fun delete(id: Int): Boolean = update("$DELETE_BY_ID$id") > 0
}

