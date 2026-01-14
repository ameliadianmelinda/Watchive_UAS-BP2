package com.example.watchive.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist_folders")
data class WatchlistFolder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int, // Menghubungkan ke User
    val title: String,
    val description: String?
)
