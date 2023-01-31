package app.service.exhibit.look

import app.model.exhibit.look.Look
import app.service.exhibit.ExhibitService
import io.micronaut.http.multipart.StreamingFileUpload
import io.reactivex.rxjava3.core.Single
import java.util.UUID

interface LookService: ExhibitService<Look> {
    fun add(id: UUID, name: String, creatorId: UUID, description: String?, isPrivate: Boolean, content: StreamingFileUpload?): Single<Boolean>
    @Deprecated("")
    fun add(item: Look, content: StreamingFileUpload?): Single<Boolean>
}
