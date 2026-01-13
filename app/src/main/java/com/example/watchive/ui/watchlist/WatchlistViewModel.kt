package com.example.watchive.ui.watchlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.watchive.data.local.WatchlistFolder
import com.example.watchive.data.local.WatchlistMovie
import com.example.watchive.data.local.WatchlistRepository
import kotlinx.coroutines.launch

class WatchlistViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = WatchlistRepository.create(application)

    val watchlist: LiveData<List<WatchlistMovie>> = repo.getAll()
    val folders: LiveData<List<WatchlistFolder>> = repo.getFolders()

    fun add(movie: com.example.watchive.data.remote.model.Movie) = viewModelScope.launch {
        repo.add(movie)
    }

    fun remove(movieId: Int) = viewModelScope.launch {
        repo.remove(movieId)
    }

    suspend fun exists(movieId: Int): Boolean {
        return repo.exists(movieId)
    }
}
