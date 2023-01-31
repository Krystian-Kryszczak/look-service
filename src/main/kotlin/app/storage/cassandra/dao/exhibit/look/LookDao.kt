package app.storage.cassandra.dao.exhibit.look

import app.model.exhibit.look.Look
import app.storage.cassandra.dao.exhibit.ExhibitDao
import com.datastax.dse.driver.api.core.cql.reactive.ReactiveResultSet
import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet
import com.datastax.oss.driver.api.mapper.annotations.Dao
import com.datastax.oss.driver.api.mapper.annotations.Delete
import java.util.UUID

@Dao
interface LookDao: ExhibitDao<Look> {
    @Delete(entityClass = [Look::class], customWhereClause = "id = :id")
    fun deleteByIdReactive(id: UUID): ReactiveResultSet
    @Delete(entityClass = [Look::class], customWhereClause = "id = :id", ifExists = true)
    fun deleteByIdIfExistsReactive(id: UUID): MappedReactiveResultSet<Boolean>
}
