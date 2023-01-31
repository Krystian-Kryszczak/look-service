package app.service

import app.model.Item
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import java.util.*

interface ItemService<T: Item> {
    fun save(item: T): Completable
    fun findById(id: UUID): Maybe<T>
    fun update(item: T): Completable
    fun delete(item: T): Completable
    fun deleteById(id: UUID): Completable
    fun deleteByIdIfExists(id: UUID): Single<Boolean>
}
