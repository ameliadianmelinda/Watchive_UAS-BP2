package com.example.watchive.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWatchlist(watchlist: Watchlist)

    @Query("SELECT * FROM watchlist WHERE userId = :userId")
    fun getWatchlistForUser(userId: Int): Flow<List<Watchlist>>

    @Query("SELECT count(*) FROM watchlist WHERE userId = :userId AND movieId = :movieId")
    suspend fun isOnWatchlist(userId: Int, movieId: Int): Int

    @Query("DELETE FROM watchlist WHERE userId = :userId AND movieId = :movieId")
    suspend fun removeFromWatchlist(userId: Int, movieId: Int)
}
