package app.storage.cassandra.dao.being.user

import app.model.being.user.User
import app.storage.cassandra.dao.being.BeingDao
import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet
import com.datastax.oss.driver.api.mapper.annotations.Dao
import com.datastax.oss.driver.api.mapper.annotations.Select

@Dao
interface UserDao: BeingDao<User> {
    @Select(customWhereClause = "email = :email", limit = "1", allowFiltering = true)
    fun findByEmailReactive(email: String): MappedReactiveResultSet<User>
}
