package com.racing.config

import com.racing.db.Database
import io.ktor.server.application.*

fun Application.configureDatabase() {
    val dbConfig = environment.config.config("database")
    Database.init(dbConfig)
}