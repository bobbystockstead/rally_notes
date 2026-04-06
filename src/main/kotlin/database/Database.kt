package com.racer.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import javax.sql.DataSource

/**
 * Database module for Ktor application.
 * Manages HikariCP connection pool for MySQL.
 */
object DatabaseFactory {
    private lateinit var dataSource: HikariDataSource

    fun init(config: DatabaseConfig) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = "jdbc:mysql://${config.host}:${config.port}/${config.database}"
            username = config.username
            password = config.password
            maximumPoolSize = config.poolSize
            minimumIdle = config.minimumIdle
            connectionTimeout = config.connectionTimeout
            idleTimeout = config.idleTimeout
            maxLifetime = config.maxLifetime
            driverClassName = "com.mysql.cj.jdbc.Driver"
        }
        dataSource = HikariDataSource(hikariConfig)
    }

    fun getDataSource(): DataSource {
        if (!::dataSource.isInitialized) {
            throw IllegalStateException("Database not initialized. Call DatabaseFactory.init() during application setup.")
        }
        return dataSource
    }

    fun close() {
        if (::dataSource.isInitialized && !dataSource.isClosed) {
            dataSource.close()
        }
    }
}

data class DatabaseConfig(
    val host: String = "localhost",
    val port: Int = 3306,
    val database: String = "rally_notes",
    val username: String = "root",
    val password: String = "",
    val poolSize: Int = 20,
    val minimumIdle: Int = 5,
    val connectionTimeout: Long = 30000,
    val idleTimeout: Long = 600000,
    val maxLifetime: Long = 1800000
)

/**
 * Ktor plugin to configure database on application startup.
 * Usage in Application.kt: configureDatabaseFactory()
 */
fun Application.configureDatabaseFactory() {
    // Check if we're in test mode by checking the environment or a marker
    val isTest = environment.config.propertyOrNull("ktor.deployment.environment")?.getString() == "test" ||
                 environment.config.propertyOrNull("database.skip-init")?.getString()?.toBoolean() ?: false

    if (isTest) {
        log.info("Running in test mode, skipping database initialization")
        return
    }

    val dbHost = environment.config.propertyOrNull("database.host")?.getString() ?: "localhost"
    val dbPort = environment.config.propertyOrNull("database.port")?.getString()?.toInt() ?: 3306
    val dbName = environment.config.propertyOrNull("database.name")?.getString() ?: "rally_notes"
    val dbUser = environment.config.propertyOrNull("database.username")?.getString() ?: "root"
    val dbPassword = environment.config.propertyOrNull("database.password")?.getString() ?: ""
    val poolSize = environment.config.propertyOrNull("database.pool-size")?.getString()?.toInt() ?: 20

    val config = DatabaseConfig(
        host = dbHost,
        port = dbPort,
        database = dbName,
        username = dbUser,
        password = dbPassword,
        poolSize = poolSize
    )

    DatabaseFactory.init(config)
    log.info("Database connection pool initialized: jdbc:mysql://$dbHost:$dbPort/$dbName")

    // Close the data source on application shutdown
    environment.monitor.subscribe(ApplicationStopping) {
        DatabaseFactory.close()
        log.info("Database connection pool closed")
    }
}



