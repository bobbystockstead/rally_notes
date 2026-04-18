package com.racing.db

import com.racing.data.Car
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS

private val logger = LoggerFactory.getLogger("CarRepository")

class CarRepository {

    fun getAll() : List<Car> {
        val sql = "SELECT car_id, name, model_id FROM car ORDER BY car_id;"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { resultSet ->
                    val cars = mutableListOf<Car>()
                    while (resultSet.next()) {
                        cars += mapCar(resultSet)
                    }
                    logger.debug("Retrieved ${cars.size} cars from database")
                    return cars
                }
            }
        }
    }

    fun getById(id: Int): Car? {
        val sql = "SELECT car_id, name, model_id FROM car WHERE car_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        val car = mapCar(resultSet)
                        logger.debug("Retrieved car with ID $id: ${car.name}")
                        car
                    } else {
                        logger.debug("Car with ID $id not found")
                        null
                    }
                }
            }
        }
    }

    fun create(car: Car): Int {
        val sql = "INSERT INTO car (name, model_id) VALUES (?, ?)"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql, RETURN_GENERATED_KEYS).use { statement ->
                statement.setString(1, car.name)
                statement.setObject(2, car.model_id)
                val rows = statement.executeUpdate()
                if (rows == 0) {
                    logger.debug("Failed creating new car: ${car.name}")
                    throw SQLException("Insert failed, no rows created")
                }

                statement.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val newId = keys.getInt(1)
                        logger.info("Created new car: ID=$newId, name=${car.name}")
                        return newId
                    } else {
                        logger.debug("Insert succeeded but no generated keys found for car: ${car.name}")
                        throw SQLException("Insert succeeded but no generated keys found")
                    }
                }
            }
        }
    }

    fun update(id: Int, car: Car): Int {
        val sql = "UPDATE car SET name = ?, model_id = ? WHERE car_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, car.name)
                statement.setObject(2, car.model_id)
                statement.setInt(3, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Updated car with ID $id: ${car.name}")
                } else {
                    logger.debug("Update for car ID $id had no effect (car not found)")
                }
                return rows
            }
        }
    }

    fun delete(id: Int): Int {
        val sql = "DELETE FROM car WHERE car_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Deleted car with ID $id")
                } else {
                    logger.debug("Delete for car ID $id had no effect (car not found)")
                }
                return rows
            }
        }
    }
    private fun mapCar(rs: ResultSet) = Car(
        car_id = rs.getInt("car_id"),
        name = rs.getString("name"),
        model_id = rs.getObject("model_id") as? Int
    )
}