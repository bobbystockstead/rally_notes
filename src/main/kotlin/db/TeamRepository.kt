package com.racing.db

import com.racing.data.Team

class TeamRepository {
    fun getById(id: Int): Team? {
        val sql = "SELECT id, name FROM teams WHERE id = ?"

        Database.dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { rs ->
                    return if (rs.next()) {
                        Team(
                            id = rs.getInt("id"),
                            name = rs.getString("name")
                        )
                    } else null
                }
            }
        }
    }

}