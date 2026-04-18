package com.racing

import com.racing.config.*
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
