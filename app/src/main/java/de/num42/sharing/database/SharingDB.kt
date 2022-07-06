package de.num42.sharing.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import de.num42.sharing.database.daos.ItemDAO
import de.num42.sharing.database.entities.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*


@Database(entities = arrayOf(Item::class), version = 1, exportSchema = false)
abstract class SharingDB : RoomDatabase() {

    abstract fun itemDAO(): ItemDAO

    private class SharingDatabaseCallback(private val scope: CoroutineScope): RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase){
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                }
            }
        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: SharingDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SharingDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SharingDB::class.java,
                    "sharing_database"
                )
                    .addCallback(SharingDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance

            }
        }
    }
}