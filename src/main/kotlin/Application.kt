package com.racing

import com.racing.config.configureDatabase
import com.racing.config.configureErrorHandling
import com.racing.config.configureHTTP
import com.racing.config.configureHealth
import com.racing.config.configureOpenAPI
import com.racing.config.configureRouting
import com.racing.config.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    configureDatabase()
    configureErrorHandling()
    configureHealth()
    configureOpenAPI()
    configureHTTP()
    configureSerialization()
    configureRouting()
}
