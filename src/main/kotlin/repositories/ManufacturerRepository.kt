package com.racer.repositories

import com.racer.models.Manufacturer
import com.racer.database.DatabaseFactory
import javax.sql.DataSource
import java.sql.ResultSet

/**
 * Repository for Manufacturer entities.
 */
class ManufacturerRepository : BaseRepository {
    override val dataSource: DataSource
        get() = DatabaseFactory.getDataSource()

    companion object {
        private const val SELECT_ALL = "SELECT manufacturer_id, name FROM manufacturer"
        private const val SELECT_BY_ID = "SELECT manufacturer_id, name FROM manufacturer WHERE manufacturer_id = "
        private const val INSERT = "INSERT INTO manufacturer (name) VALUES "
        private const val UPDATE_BY_ID = "UPDATE manufacturer SET name = '%s' WHERE manufacturer_id = %d"
        private const val DELETE_BY_ID = "DELETE FROM manufacturer WHERE manufacturer_id = "

        private fun mapRowToManufacturer(rs: ResultSet): Manufacturer = Manufacturer(
            manufacturerId = rs.getInt("manufacturer_id"),
            name = rs.getString("name")
        )
    }

    fun findAll(): List<Manufacturer> = queryForList(SELECT_ALL) { mapRowToManufacturer(it) }

    fun findById(id: Int): Manufacturer =
        queryForObject("$SELECT_BY_ID$id") { mapRowToManufacturer(it) }
            ?: throw RepositoryException.NotFoundException("Manufacturer with id $id not found")

    fun create(name: String): Int {
        val escapedName = name.replace("'", "\\'")
        val sql = "$INSERT ('$escapedName')"

        return withConnection { conn ->
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.executeUpdate()
                stmt.generatedKeys.use { rs ->
                    if (rs.next()) rs.getInt(1) else throw RepositoryException.DataAccessException("Failed to retrieve generated id")
                }
            }
        }
    }

    fun update(id: Int, name: String): Boolean {
        val escapedName = name.replace("'", "\\'")
        val sql = String.format(UPDATE_BY_ID, escapedName, id)
        return update(sql) > 0
    }

    fun delete(id: Int): Boolean = update("$DELETE_BY_ID$id") > 0
}

