package app.service.being.user

import app.model.being.user.User
import app.storage.cassandra.dao.being.user.UserDao
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import jakarta.inject.Singleton
import java.util.UUID

@Singleton
class UserServiceCassandra(private val userDao: UserDao): UserService {
    override fun saveAsync(user: User): Completable = Completable.fromPublisher(userDao.saveReactive(user))
    override fun findByIdAsync(id: UUID): Maybe<User> = Maybe.fromPublisher(userDao.findByIdReactive(id))
    override fun findByEmailAsync(email: String): Maybe<User> = Flowable.fromPublisher(userDao.findByEmailReactive(email)).firstElement()
}
