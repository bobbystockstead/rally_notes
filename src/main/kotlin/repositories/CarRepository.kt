package com.racer.repositories

import com.racer.models.Car
import com.racer.database.DatabaseFactory
import javax.sql.DataSource
import java.sql.ResultSet

/**
 * Repository for Car entities.
 */
class CarRepository : BaseRepository {
    override val dataSource: DataSource
        get() = DatabaseFactory.getDataSource()

    companion object {
        private const val SELECT_ALL = "SELECT car_id, name, model_id FROM car"
        private const val SELECT_BY_ID = "SELECT car_id, name, model_id FROM car WHERE car_id = "
        private const val SELECT_BY_MODEL = "SELECT car_id, name, model_id FROM car WHERE model_id = "
        private const val INSERT = "INSERT INTO car (name, model_id) VALUES "
        private const val UPDATE_BY_ID = "UPDATE car SET name = '%s', model_id = %s WHERE car_id = %d"
        private const val DELETE_BY_ID = "DELETE FROM car WHERE car_id = "

        private fun mapRowToCar(rs: ResultSet): Car = Car(
            carId = rs.getInt("car_id"),
            name = rs.getString("name"),
            modelId = rs.getObject("model_id") as? Int
        )
    }

    fun findAll(): List<Car> = queryForList(SELECT_ALL) { mapRowToCar(it) }

    fun findById(id: Int): Car =
        queryForObject("$SELECT_BY_ID$id") { mapRowToCar(it) }
            ?: throw RepositoryException.NotFoundException("Car with id $id not found")

    fun findByModel(modelId: Int): List<Car> =
        queryForList("$SELECT_BY_MODEL$modelId") { mapRowToCar(it) }

    fun create(name: String, modelId: Int?): Int {
        val escapedName = name.replace("'", "\\'")
        val modelIdVal = modelId?.toString() ?: "NULL"
        val sql = "$INSERT ('$escapedName', $modelIdVal)"

        return withConnection { conn ->
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.executeUpdate()
                stmt.generatedKeys.use { rs ->
                    if (rs.next()) rs.getInt(1) else throw RepositoryException.DataAccessException("Failed to retrieve generated id")
                }
            }
        }
    }

    fun update(id: Int, name: String, modelId: Int?): Boolean {
        val escapedName = name.replace("'", "\\'")
        val modelIdVal = modelId?.toString() ?: "NULL"
        val sql = String.format(UPDATE_BY_ID, escapedName, modelIdVal, id)
        return update(sql) > 0
    }

    fun delete(id: Int): Boolean = update("$DELETE_BY_ID$id") > 0
}

