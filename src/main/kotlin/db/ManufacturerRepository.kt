package com.racing.db

import com.racing.data.Manufacturer
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS

private val logger = LoggerFactory.getLogger("ManufacturerRepository")

class ManufacturerRepository {

    fun getAll() : List<Manufacturer> {
        val sql = "SELECT manufacturer_id, name FROM manufacturer ORDER BY manufacturer_id;"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { resultSet ->
                    val manufacturers = mutableListOf<Manufacturer>()
                    while (resultSet.next()) {
                        manufacturers += mapManufacturer(resultSet)
                    }
                    logger.debug("Retrieved ${manufacturers.size} manufacturers from database")
                    return manufacturers
                }
            }
        }
    }

    fun getById(id: Int): Manufacturer? {
        val sql = "SELECT manufacturer_id, name FROM manufacturer WHERE manufacturer_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        val manufacturer = mapManufacturer(resultSet)
                        logger.debug("Retrieved manufacturer with ID $id: ${manufacturer.name}")
                        manufacturer
                    } else {
                        logger.debug("Manufacturer with ID $id not found")
                        null
                    }
                }
            }
        }
    }

    fun create(manufacturer: Manufacturer): Int {
        val sql = "INSERT INTO manufacturer (name) VALUES (?)"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql, RETURN_GENERATED_KEYS).use { statement ->
                statement.setString(1, manufacturer.name)
                val rows = statement.executeUpdate()
                if (rows == 0) {
                    logger.debug("Failed creating new manufacturer: ${manufacturer.name}")
                    throw SQLException("Insert failed, no rows created")
                }

                statement.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val newId = keys.getInt(1)
                        logger.info("Created new manufacturer: ID=$newId, name=${manufacturer.name}")
                        return newId
                    } else {
                        logger.debug("Insert succeeded but no generated keys found for manufacturer: ${manufacturer.name}")
                        throw SQLException("Insert succeeded but no generated keys found")
                    }

                }
            }
        }
    }

    fun update(id: Int, manufacturer: Manufacturer): Int {
        val sql = "UPDATE manufacturer SET name = ? WHERE manufacturer_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, manufacturer.name)
                statement.setInt(2, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Updated manufacturer with ID $id: ${manufacturer.name}")
                } else {
                    logger.debug("Update for manufacturer ID $id had no effect (manufacturer not found)")
                }
                return rows
            }
        }
    }

    fun delete(id: Int): Int {
        val sql = "DELETE FROM manufacturer WHERE manufacturer_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Deleted manufacturer with ID $id")
                } else {
                    logger.debug("Delete for manufacturer ID $id had no effect (manufacturer not found)")
                }
                return rows
            }
        }
    }
    private fun mapManufacturer(rs: ResultSet) = Manufacturer(
        manufacturer_id = rs.getInt("manufacturer_id"),
        name = rs.getString("name")
    )
}