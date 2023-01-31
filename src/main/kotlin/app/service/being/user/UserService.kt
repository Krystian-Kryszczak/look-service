package app.service.being.user

import app.model.being.user.User
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import java.util.UUID

interface UserService {
    fun saveAsync(user: User): Completable
    fun findByIdAsync(id: UUID): Maybe<User>
    fun findByEmailAsync(email: String): Maybe<User>
}
