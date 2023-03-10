package app.endpoint

import app.model.exhibit.look.Look
import app.model.exhibit.look.LookModel
import app.service.exhibit.look.LookService
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
    fun add(@Part lookModel: LookModel, @Part content: StreamingFileUpload?, authentication: Authentication): Single<out HttpResponse<UUID>> {


        val look: Look = lookModel.convert(authentication) ?: return Single.just(invalidLookModelResp)

        return lookService.add(look, content)
            .map { HttpResponse.created(it) }
            .defaultIfEmpty(invalidLookModelResp)
            .onErrorReturn {
                logger.error(it.message, it.stackTrace)
                HttpResponse.serverError()
            }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(LookEndpoint::class.java)
        private val invalidLookModelResp = HttpResponse.status<UUID>(HttpStatus.BAD_REQUEST, "Look model is invalid.")
    }
}
