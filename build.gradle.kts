val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.3.0"
    id("io.ktor.plugin") version "3.4.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.0"
}

group = "com.racer"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Ktor Core
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-default-headers")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-openapi")
    implementation("io.ktor:ktor-server-routing-openapi")
    implementation("io.ktor:ktor-server-swagger")
    implementation("io.ktor:ktor-server-csrf")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-host-common")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-config-yaml")

    // Serialization
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")

    // Monitoring & Health
    implementation("dev.hayden:khealth:3.0.2")
    implementation("io.ktor:ktor-server-metrics")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // Database
    implementation("com.mysql:mysql-connector-j:8.2.0")
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
