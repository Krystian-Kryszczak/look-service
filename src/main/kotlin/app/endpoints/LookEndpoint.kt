package app.endpoints

import app.model.exhibit.look.Look
import app.service.exhibit.look.LookService
import app.utils.SecurityUtils
import com.datastax.oss.driver.api.core.uuid.Uuids
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Part
import io.micronaut.http.annotation.Post
import io.micronaut.http.multipart.StreamingFileUpload
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import org.slf4j.LoggerFactory
import java.util.UUID

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/look")
class LookEndpoint(private val lookService: LookService) {

    @Get(consumes = [MediaType.APPLICATION_JSON])
    fun propose(authentication: Authentication?): Flowable<Look> = lookService.propose(authentication)

    @Get("/{id}")
    fun get(id: UUID, authentication: Authentication?): Maybe<Look> = lookService.findById(id, authentication)

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Post(consumes = [MediaType.MULTIPART_FORM_DATA])
    fun add(@Part look: Look, @Part content: StreamingFileUpload?, authentication: Authentication): Single<out HttpResponse<UUID>> {
        if (look.isInvalid()) return Single.just(HttpResponse.status(HttpStatus.BAD_REQUEST, "look is invalid"))

        val generatedLookId = Uuids.timeBased()
        val clientId = SecurityUtils.getClientId(authentication) ?: return Single.just(HttpResponse.status(HttpStatus.UNAUTHORIZED))

        look.id = generatedLookId
        look.creatorId = clientId

        return lookService.add(look, content)
            .map {
                HttpResponse.created(generatedLookId)
            }.onErrorReturn {
                logger.error(it.message, it.stackTrace)
                return@onErrorReturn HttpResponse.serverError()
            }
    }

    private fun Look.isInvalid(): Boolean = id != null || creatorId != null

    companion object {
        private val logger = LoggerFactory.getLogger(LookEndpoint::class.java)
    }
}
