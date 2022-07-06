package de.num42.sharing.database.daos

import androidx.room.*
import de.num42.sharing.database.entities.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDAO {
    @Query("SELECT * FROM item ORDER BY itemId")
    fun getAllItems(): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Query("DELETE FROM item" )
    fun deleteAllItems()

}