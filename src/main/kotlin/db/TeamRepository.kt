package com.racing.db

import com.racing.data.Team
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS

private val logger = LoggerFactory.getLogger("TeamRepository")

class TeamRepository {

    fun getAll() : List<Team> {
        val sql = "SELECT team_id, name, manufacturer_id FROM team ORDER BY team_id;"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { resultSet ->
                    val teams = mutableListOf<Team>()
                    while (resultSet.next()) {
                        teams += mapTeam(resultSet)
                    }
                    logger.debug("Retrieved ${teams.size} teams from database")
                    return teams
                }
            }
        }
    }

    fun getById(id: Int): Team? {
        val sql = "SELECT team_id, name, manufacturer_id FROM team WHERE team_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        val team = mapTeam(resultSet)
                        logger.debug("Retrieved team with ID $id: ${team.name}")
                        team
                    } else {
                        logger.debug("Team with ID $id not found")
                        null
                    }
                }
            }
        }
    }

    fun create(team: Team): Int {
        val sql = "INSERT INTO team (name, manufacturer_id) VALUES (?, ?)"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql, RETURN_GENERATED_KEYS).use { statement ->
                statement.setString(1, team.name)
                statement.setObject(2, team.manufacturer_id)
                val rows = statement.executeUpdate()
                if (rows == 0) {
                    logger.debug("Failed creating new team: ${team.name}")
                    throw SQLException("Insert failed, no rows created")
                }

                statement.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val newId = keys.getInt(1)
                        logger.info("Created new team: ID=$newId, name=${team.name}")
                        return newId
                    } else {
                        logger.debug("Insert succeeded but no generated keys found for team: ${team.name}")
                        throw SQLException("Insert succeeded but no generated keys found")
                    }
                }
            }
        }
    }

    fun update(id: Int, team: Team): Int {
        val sql = "UPDATE team SET name = ?, manufacturer_id = ? WHERE team_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, team.name)
                statement.setObject(2, team.manufacturer_id)
                statement.setInt(3, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Updated team with ID $id: ${team.name}")
                } else {
                    logger.debug("Update for team ID $id had no effect (team not found)")
                }
                return rows
            }
        }
    }

    fun delete(id: Int): Int {
        val sql = "DELETE FROM team WHERE team_id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                val rows = statement.executeUpdate()
                if (rows > 0) {
                    logger.info("Deleted team with ID $id")
                } else {
                    logger.debug("Delete for team ID $id had no effect (team not found)")
                }
                return rows
            }
        }
    }
    private fun mapTeam(rs: ResultSet) = Team(
        team_id = rs.getInt("team_id"),
        name = rs.getString("name"),
        manufacturer_id = rs.getObject("manufacturer_id") as? Int
    )
}