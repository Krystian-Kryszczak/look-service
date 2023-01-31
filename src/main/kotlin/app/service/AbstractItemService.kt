package app.service

import app.model.Item
import app.storage.cassandra.dao.ItemDao
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import java.util.*

abstract class AbstractItemService<T: Item>(private val itemDao: ItemDao<T>): ItemService<T> {
    override fun save(item: T): Completable = Completable.fromPublisher(itemDao.saveReactive(item))
    override fun findById(id: UUID): Maybe<T> = Maybe.fromPublisher(itemDao.findByIdReactive(id))
    override fun update(item: T): Completable = Completable.fromPublisher(itemDao.updateReactive(item))
    override fun delete(item: T): Completable = Completable.fromPublisher(itemDao.deleteReactive(item))
}
