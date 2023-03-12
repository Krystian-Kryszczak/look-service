package app.util

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.micronaut.security.authentication.ServerAuthentication
import java.util.UUID

class SecurityUtilsTest: StringSpec({

    "get client id should not return null" {
        val authentication = ServerAuthentication(
            "John Smith",
                listOf("USER"),
                mapOf(
                    "id" to UUID.randomUUID().toString()
                ) as Map<String, Any>?
        )
        SecurityUtils.getClientId(authentication) shouldNotBe null
    }

    "get client id should return null" {
        val authentication = ServerAuthentication(
                "John Smith",
                listOf("USER"),
                mapOf()
        )
        SecurityUtils.getClientId(authentication) shouldBe null
    }

    "client is creator should return true" {
        val creatorId = UUID.randomUUID()
        val authentication = ServerAuthentication(
                "John Smith",
                listOf("USER"),
                mapOf(
                    "id" to creatorId.toString()
                ) as Map<String, Any>?
        )
        SecurityUtils.clientIsCreator(creatorId, authentication) shouldBe true
    }

    "client is creator should return false" {
        val creatorId = UUID.randomUUID()
        val authentication = ServerAuthentication(
                "John Smith",
                listOf("USER"),
                mapOf(
                    "id" to UUID.randomUUID().toString()
                ) as Map<String, Any>?
        )
        SecurityUtils.clientIsCreator(creatorId, authentication) shouldBe false
    }
})
