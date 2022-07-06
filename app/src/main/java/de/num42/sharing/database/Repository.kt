package de.num42.sharing.database

import kotlinx.coroutines.flow.Flow
import androidx.annotation.WorkerThread
import de.num42.sharing.database.daos.ItemDAO
import de.num42.sharing.database.entities.Item

class Repository(private val itemDAO: ItemDAO) {
    val allItems: Flow<List<Item>> = itemDAO.getAllItems()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertItem(item: Item) {
        itemDAO.insert(item)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllItems() {
        itemDAO.deleteAllItems()
    }

}