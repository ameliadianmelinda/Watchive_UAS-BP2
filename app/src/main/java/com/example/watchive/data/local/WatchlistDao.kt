package com.example.watchive.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist WHERE userId = :userId")
    fun getAll(userId: Int): LiveData<List<WatchlistMovie>>

    @Query("SELECT * FROM watchlist WHERE userId = :userId")
    suspend fun getAllStatic(userId: Int): List<WatchlistMovie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: WatchlistMovie)

    // Optimasi: Insert banyak film sekaligus
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<WatchlistMovie>)

    @Query("DELETE FROM watchlist WHERE id = :movieId AND userId = :userId")
    suspend fun deleteById(movieId: Int, userId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE id = :movieId AND userId = :userId)")
    suspend fun exists(movieId: Int, userId: Int): Boolean
}
