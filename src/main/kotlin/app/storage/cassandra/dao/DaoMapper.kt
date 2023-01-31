package app.storage.cassandra.dao

import app.storage.cassandra.dao.being.user.UserDao
import app.storage.cassandra.dao.exhibit.look.LookDao
import com.datastax.oss.driver.api.core.CqlIdentifier
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.mapper.MapperBuilder
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace
import com.datastax.oss.driver.api.mapper.annotations.Mapper

@Mapper
interface DaoMapper {
    @DaoFactory
    fun userDao(@DaoKeyspace keyspace: CqlIdentifier): UserDao
    @DaoFactory
    fun lookDao(@DaoKeyspace keyspace: CqlIdentifier): LookDao
    companion object {
        @JvmStatic
        fun builder(session: CqlSession): MapperBuilder<DaoMapper> {
            return DaoMapperBuilder(session)
        }
    }
}
