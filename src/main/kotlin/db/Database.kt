package com.racing.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*

object Database {

    lateinit var dataSource: HikariDataSource
        private set

    fun init(config: ApplicationConfig) {
        val host = config.property("host").getString()
        val port = config.property("port").getString().toInt()
        val name: String = config.property("name").getString()

        dataSource = HikariDataSource(HikariConfig().apply {
            jdbcUrl = "jdbc:mysql://$host:$port/$name"
            username = config.property("username").getString()
            password = config.property("password").getString()
            maximumPoolSize = config.property("pool-size").getString().toInt()
        })
    }

}
