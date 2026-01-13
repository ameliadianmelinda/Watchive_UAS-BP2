package com.example.watchive.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update

@Dao
interface WatchlistFolderDao {
    @Query("SELECT * FROM watchlist_folders")
    fun getAllFolders(): LiveData<List<WatchlistFolder>>

    @Query("SELECT * FROM watchlist_folders")
    suspend fun getAllFoldersStatic(): List<WatchlistFolder>

    @Query("SELECT * FROM watchlist_folders WHERE id = :folderId")
    suspend fun getFolderById(folderId: Int): WatchlistFolder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: WatchlistFolder)

    @Update
    suspend fun updateFolder(folder: WatchlistFolder)

    @Delete
    suspend fun deleteFolder(folder: WatchlistFolder)

    // Folder-Movie Joins
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMovieToFolder(join: FolderMovieJoin)

    @Query("DELETE FROM folder_movie_join WHERE folderId = :folderId AND movieId = :movieId")
    suspend fun removeMovieFromFolder(folderId: Int, movieId: Int)

    @Query("""
        SELECT watchlist.* FROM watchlist 
        INNER JOIN folder_movie_join ON watchlist.id = folder_movie_join.movieId 
        WHERE folder_movie_join.folderId = :folderId
    """)
    fun getMoviesInFolder(folderId: Int): LiveData<List<WatchlistMovie>>

    @Query("""
        SELECT watchlist.* FROM watchlist 
        INNER JOIN folder_movie_join ON watchlist.id = folder_movie_join.movieId 
        WHERE folder_movie_join.folderId = :folderId
    """)
    suspend fun getMoviesInFolderStatic(folderId: Int): List<WatchlistMovie>
}
