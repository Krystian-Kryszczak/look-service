package app.service.exhibit.look

import app.model.exhibit.look.Look
import app.service.exhibit.ExhibitService
import io.micronaut.http.multipart.StreamingFileUpload
import io.reactivex.rxjava3.core.Maybe
import java.util.UUID

interface LookService: ExhibitService<Look> {
    fun add(item: Look, content: StreamingFileUpload?): Maybe<UUID>
}
