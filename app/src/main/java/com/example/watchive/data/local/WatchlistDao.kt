package com.example.watchive.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist")
    fun getAll(): LiveData<List<WatchlistMovie>>

    @Query("SELECT * FROM watchlist")
    suspend fun getAllStatic(): List<WatchlistMovie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: WatchlistMovie)

    @Query("DELETE FROM watchlist WHERE id = :movieId")
    suspend fun deleteById(movieId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE id = :movieId)")
    suspend fun exists(movieId: Int): Boolean
}
