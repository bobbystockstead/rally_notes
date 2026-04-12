package com.racing

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testHealth() = testApplication {
        application {
            testModule()
        }
        client.get("/health").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

}
