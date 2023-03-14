package app.service.being.user

import app.model.being.user.User
import app.service.ItemService
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import java.util.UUID

interface UserService: ItemService<User> {
    fun saveReactive(user: User): Completable
    fun findByIdReactive(id: UUID): Maybe<User>
    fun findByEmailReactive(email: String): Maybe<User>
}
