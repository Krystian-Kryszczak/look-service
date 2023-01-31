package app.storage.cassandra.dao

import app.model.Item
import com.datastax.dse.driver.api.mapper.reactive.MappedReactiveResultSet
import com.datastax.oss.driver.api.mapper.annotations.Select
import java.util.UUID

interface ItemDao<T: Item>: BaseDao<T> {
    @Select
    fun findByIdReactive(id: UUID): MappedReactiveResultSet<T>
}
