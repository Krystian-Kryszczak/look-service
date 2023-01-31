package app.service.exhibit

import app.model.exhibit.Exhibit
import app.service.ItemService
import io.micronaut.security.authentication.Authentication
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import java.util.UUID

interface ExhibitService<T: Exhibit>: ItemService<T> {
    fun propose(authentication: Authentication?): Flowable<T>
    fun findById(id: UUID, authentication: Authentication?): Maybe<T>
    fun checkIfIsPrivate(id: UUID): Single<Boolean>
    fun getCreatorId(itemId: UUID): Maybe<UUID>
}
