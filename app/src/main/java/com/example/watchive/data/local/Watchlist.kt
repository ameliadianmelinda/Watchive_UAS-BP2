package com.example.watchive.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "watchlist",
    // We add an index on userId and movieId to make lookups faster
    indices = [Index(value = ["userId"]), Index(value = ["movieId"])],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Watchlist(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val movieId: Int,
    val title: String,
    val posterPath: String?,
    val rating: Double
)
