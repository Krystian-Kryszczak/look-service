package app.storage.cassandra.dao.being.user

import app.model.being.user.User
import app.storage.cassandra.dao.being.BeingDao
import com.datastax.dse.driver.api.core.cql.reactive.ReactiveResultSet
import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet
import com.datastax.oss.driver.api.mapper.annotations.Dao
import com.datastax.oss.driver.api.mapper.annotations.Delete
import com.datastax.oss.driver.api.mapper.annotations.Select
import java.util.UUID

@Dao
interface UserDao: BeingDao<User> {
    @Select(customWhereClause = "email = :email", limit = "1", allowFiltering = true)
    fun findByEmailReactive(email: String): MappedReactiveResultSet<User>
    @Delete(entityClass = [User::class])
    fun deleteByIdReactive(id: UUID): ReactiveResultSet
    @Delete(entityClass = [User::class], ifExists = true)
    fun deleteByIdIfExistsReactive(id: UUID): MappedReactiveResultSet<Boolean>
}
