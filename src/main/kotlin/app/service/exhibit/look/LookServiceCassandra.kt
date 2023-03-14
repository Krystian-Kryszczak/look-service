package app.service.exhibit.look

import app.model.exhibit.look.Look
import app.service.blob.media.image.ImageBlobService
import app.service.exhibit.AbstractExhibitService
import app.storage.cassandra.dao.exhibit.look.LookDao
import app.util.SecurityUtils
import io.micronaut.http.multipart.StreamingFileUpload
import io.micronaut.security.authentication.Authentication
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import jakarta.inject.Singleton
import java.util.UUID

@Singleton
class LookServiceCassandra(
    private val lookDao: LookDao,
    private val imageBlobService: ImageBlobService
): LookService, AbstractExhibitService<Look>(lookDao) {
    override fun propose(authentication: Authentication?): Flowable<Look> = Flowable.fromPublisher(lookDao.findReactive(10))

    override fun findById(id: UUID, authentication: Authentication?): Maybe<Look> =
        findById(id).filter { it.canUserSeeIt(authentication) }

    private fun Look.canUserSeeIt(authentication: Authentication?): Boolean = !private || SecurityUtils.clientIsCreator(creatorId, authentication)

    override fun add(item: Look, content: StreamingFileUpload?): Maybe<UUID> {
        val itemId = item.id ?: return Maybe.empty()
        val itemCreatorId = item.creatorId ?: return Maybe.empty()

        item.hasImages = content != null

        return save(item)
            .toSingleDefault(content != null)
            .flatMapMaybe {
                if (content != null) {
                    imageBlobService.save(itemId, content, itemCreatorId, item.private)
                    .flatMapMaybe { Maybe.just(item.id) }
                } else {
                    Maybe.empty()
                }
            }
    }
    override fun deleteByIdReactive(id: UUID): Completable = Completable.fromPublisher(lookDao.deleteByIdReactive(id))
    override fun deleteByIdIfExistsReactive(id: UUID): Single<Boolean> = Single.fromPublisher(lookDao.deleteByIdIfExistsReactive(id))
}
