package app.storage.cassandra.dao.being

import app.model.being.Being
import app.storage.cassandra.dao.ItemDao

interface BeingDao<T: Being>: ItemDao<T>
