package com.racer.repositories

import java.sql.Connection

/**
 * Base exception for database access operations.
 */
sealed class RepositoryException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NotFoundException(message: String) : RepositoryException(message)
    class DataAccessException(message: String, cause: Throwable? = null) : RepositoryException(message, cause)
    class ValidationException(message: String) : RepositoryException(message)
    class NotInitializedException(message: String) : RepositoryException(message)
}

/**
 * Base interface for all repositories.
 * Provides common database operations.
 */
interface BaseRepository {
    val dataSource: javax.sql.DataSource

    fun <T> queryForList(sql: String, mapper: (java.sql.ResultSet) -> T): List<T> {
        return try {
            dataSource.connection.use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.executeQuery(sql).use { rs ->
                        val results = mutableListOf<T>()
                        while (rs.next()) {
                            results.add(mapper(rs))
                        }
                        results
                    }
                }
            }
        } catch (e: Exception) {
            throw RepositoryException.DataAccessException("Failed to execute query", e)
        }
    }

    fun <T> queryForObject(sql: String, mapper: (java.sql.ResultSet) -> T?): T? {
        return try {
            dataSource.connection.use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.executeQuery(sql).use { rs ->
                        if (rs.next()) mapper(rs) else null
                    }
                }
            }
        } catch (e: Exception) {
            throw RepositoryException.DataAccessException("Failed to execute query", e)
        }
    }

    fun update(sql: String): Int {
        return try {
            dataSource.connection.use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.executeUpdate(sql)
                }
            }
        } catch (e: Exception) {
            throw RepositoryException.DataAccessException("Failed to execute update", e)
        }
    }

    fun <T> withConnection(block: (Connection) -> T): T {
        return try {
            dataSource.connection.use(block)
        } catch (e: Exception) {
            throw RepositoryException.DataAccessException("Database operation failed", e)
        }
    }
}



