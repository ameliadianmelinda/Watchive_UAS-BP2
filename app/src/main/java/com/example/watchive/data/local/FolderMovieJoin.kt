package com.example.watchive.data.local

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "folder_movie_join",
    primaryKeys = ["folderId", "movieId"],
    foreignKeys = [
        ForeignKey(
            entity = WatchlistFolder::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WatchlistMovie::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FolderMovieJoin(
    val folderId: Int,
    val movieId: Int
)
