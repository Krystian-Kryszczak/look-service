package app.service.exhibit.look

import app.model.exhibit.look.Look
import app.service.being.user.UserService
import app.service.blob.media.image.ImageBlobService
import app.service.blob.media.video.VideoBlobService
import app.service.exhibit.AbstractExhibitService
import app.storage.cassandra.dao.exhibit.look.LookDao
import app.utils.SecurityUtils
import io.micronaut.http.multipart.StreamingFileUpload
import io.micronaut.security.authentication.Authentication
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.util.UUID

@Singleton
class LookServiceCassandra(
    private val lookDao: LookDao,
    private val userService: UserService,
    @Named("local") private val imageBlobService: ImageBlobService
): LookService, AbstractExhibitService<Look>(lookDao) {
    override fun propose(authentication: Authentication?): Flowable<Look> = Flowable.fromPublisher(lookDao.findReactive(10)).flatMapSingle { look ->
        val creatorId = look.creatorId
        return@flatMapSingle if (creatorId != null) userService.findByIdAsync(creatorId).map { user ->
            look.creatorName = (user.name ?: "·") + " " + (user.lastname ?: "·")
            return@map look
        }.defaultIfEmpty(look)
        else {
            Single.just(look)
        }
    }

    override fun findById(id: UUID, authentication: Authentication?): Maybe<Look> =
        findById(id)
        .filter {
            !it.private || SecurityUtils.clientIsCreator(it.creatorId, authentication)
        }
        .flatMap { look ->
            val creatorId = look.creatorId
            if (creatorId != null) userService.findByIdAsync(creatorId).map { user ->
                look.creatorName = (user.name ?: "·") + " " + (user.lastname ?: "·")
                return@map look
            } else Maybe.empty()
        }

    override fun add(id: UUID, name: String, creatorId: UUID, description: String?, isPrivate: Boolean, content: StreamingFileUpload?): Single<Boolean> {
        return save(Look(id, name, creatorId, description, content != null, 0, 0, isPrivate))
            .andThen(if (content != null) imageBlobService.save(id, content, creatorId, isPrivate) else Single.just(true))
    }
    @Deprecated("")
    override fun add(item: Look, content: StreamingFileUpload?): Single<Boolean> {
        if (item.id == null || item.creatorId == null) return Single.just(false)
        return save(item)
            .andThen(if (content != null) imageBlobService.save(item.id!!, content, item.creatorId!!, item.private) else Single.just(true))
    }
    override fun deleteById(id: UUID): Completable = Completable.fromPublisher(lookDao.deleteByIdReactive(id))
    override fun deleteByIdIfExists(id: UUID): Single<Boolean> = Single.fromPublisher(lookDao.deleteByIdIfExistsReactive(id))
}
