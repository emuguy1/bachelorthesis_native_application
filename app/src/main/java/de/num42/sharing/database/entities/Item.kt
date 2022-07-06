package de.num42.sharing.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    indices = [Index("id")]
)
data class Item (
    var name: String,
    var description: String,
    val itemId: String
        ){
    @PrimaryKey(autoGenerate = true)
    public var id: Int = 0
}

