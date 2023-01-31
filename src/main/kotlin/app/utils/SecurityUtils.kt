package app.utils

import io.micronaut.security.authentication.Authentication
import java.util.UUID

object SecurityUtils {
    fun getClientId(authentication: Authentication): UUID? {
        return UUID.fromString(authentication.attributes["id"] as String? ?: return null)
    }
    fun clientIsCreator(creatorId: UUID?, authentication: Authentication?): Boolean {
        val clientId = authentication?.attributes?.get("id")
        // check if client is it media creator
        return clientId is String && creatorId!=null && creatorId.toString() == clientId
    }
}
