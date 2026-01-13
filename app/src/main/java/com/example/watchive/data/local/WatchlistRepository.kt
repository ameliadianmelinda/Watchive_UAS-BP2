package com.example.watchive.data.local

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.watchive.data.remote.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WatchlistRepository(private val dao: WatchlistDao, private val folderDao: WatchlistFolderDao) {

    fun getAll(): LiveData<List<WatchlistMovie>> = dao.getAll()

    suspend fun add(movie: Movie) = withContext(Dispatchers.IO) {
        dao.insert(WatchlistMovie.fromMovie(movie))
    }

    suspend fun remove(movieId: Int) = withContext(Dispatchers.IO) {
        dao.deleteById(movieId)
    }

    suspend fun exists(movieId: Int): Boolean = withContext(Dispatchers.IO) {
        dao.exists(movieId)
    }

    // Folders
    fun getFolders(): LiveData<List<WatchlistFolder>> = folderDao.getAllFolders()

    companion object {
        fun create(context: Context): WatchlistRepository {
            val db = AppDatabase.getInstance(context)
            return WatchlistRepository(db.watchlistDao(), db.folderDao())
        }
    }
}
