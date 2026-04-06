package com.racer

import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> !call.request.path().startsWith("/favicon") }
        format { call ->
            val status = call.response.status()
            val method = call.request.httpMethod.value
            val path = call.request.path()
            val statusColor = when {
                status?.value ?: 0 < 300 -> "✓"
                status?.value ?: 0 < 400 -> "→"
                status?.value ?: 0 < 500 -> "⚠"
                else -> "✗"
            }
            "$statusColor $method $path -> $status"
        }
    }
}