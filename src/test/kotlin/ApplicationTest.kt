package com.racer

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test

class ApplicationTest {

    @Test
    fun testApplicationWorks() = testApplication {
        // Simple sanity check that the application initializes
        client.get("/")
    }

}
