package app.model.exhibit.look

import app.model.exhibit.ExhibitModel
import app.util.SecurityUtils
import com.datastax.oss.driver.api.core.uuid.Uuids
import io.micronaut.core.annotation.Introspected
import io.micronaut.security.authentication.Authentication
import java.util.UUID

@Introspected
class LookModel(
    name: String? = null,
    private val description: String? = null,
    private val hasImages: Boolean = false,
    private val views: Int = 0,
    private val rating: Int = 0,
    private: Boolean = false
): ExhibitModel<Look>(name, private) {
    override fun convert(authentication: Authentication): Look? {
        val clientId: UUID = SecurityUtils.getClientId(authentication) ?: return null
        return Look(
            Uuids.timeBased(),
            name,
            clientId,
            description,
            hasImages,
            views,
            rating,
            private
        )
    }
}
