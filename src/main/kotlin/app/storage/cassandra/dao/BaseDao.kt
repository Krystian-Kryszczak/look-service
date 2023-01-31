package app.storage.cassandra.dao

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveResultSet
import com.datastax.oss.driver.api.mapper.annotations.DefaultNullSavingStrategy
import com.datastax.oss.driver.api.mapper.annotations.Delete
import com.datastax.oss.driver.api.mapper.annotations.Insert
import com.datastax.oss.driver.api.mapper.annotations.Update
import com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy

@DefaultNullSavingStrategy(NullSavingStrategy.DO_NOT_SET)
interface BaseDao<T> {
    @Insert
    fun saveReactive(item: T): ReactiveResultSet
    @Update
    fun updateReactive(item: T): ReactiveResultSet
    @Delete
    fun deleteReactive(item: T): ReactiveResultSet
}
