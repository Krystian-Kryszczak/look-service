package app.storage.cassandra.dao.exhibit

import app.model.exhibit.Exhibit
import app.storage.cassandra.dao.ItemDao
import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet
import com.datastax.oss.driver.api.mapper.annotations.CqlName
import com.datastax.oss.driver.api.mapper.annotations.Select
import java.util.UUID

interface ExhibitDao<T: Exhibit>: ItemDao<T> {
    @Select(customWhereClause = "id = :id AND private = true", limit = "1", allowFiltering = true)
    fun findByIdIfPrivateIsEqualToTrueReactive(id: UUID): MappedReactiveResultSet<T>
    @Select(customWhereClause = "private = false", limit = ":l", allowFiltering = true)
    fun findReactive(@CqlName("l") l: Int): MappedReactiveResultSet<T>
}
