package com.example.watchive.data.local

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.watchive.data.remote.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WatchlistRepository(private val dao: WatchlistDao, private val folderDao: WatchlistFolderDao) {

    fun getAll(userId: Int): LiveData<List<WatchlistMovie>> = dao.getAll(userId)

    suspend fun add(movie: Movie, userId: Int) = withContext(Dispatchers.IO) {
        dao.insert(WatchlistMovie.fromMovie(movie, userId))
    }

    suspend fun remove(movieId: Int, userId: Int) = withContext(Dispatchers.IO) {
        dao.deleteById(movieId, userId)
    }

    suspend fun exists(movieId: Int, userId: Int): Boolean = withContext(Dispatchers.IO) {
        dao.exists(movieId, userId)
    }

    // Folders
    fun getFolders(userId: Int): LiveData<List<WatchlistFolder>> = folderDao.getAllFolders(userId)

    companion object {
        fun create(context: Context): WatchlistRepository {
            val db = AppDatabase.getInstance(context)
            return WatchlistRepository(db.watchlistDao(), db.folderDao())
        }
    }
}
