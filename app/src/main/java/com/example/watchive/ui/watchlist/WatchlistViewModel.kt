package com.example.watchive.ui.watchlist

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.watchive.data.local.WatchlistFolder
import com.example.watchive.data.local.WatchlistMovie
import com.example.watchive.data.local.WatchlistRepository
import kotlinx.coroutines.launch

class WatchlistViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = WatchlistRepository.create(application)
    private val sharedPref = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    
    private val _userId = MutableLiveData<Int>().apply {
        value = sharedPref.getInt("user_id", -1)
    }

    val watchlist: LiveData<List<WatchlistMovie>> = _userId.switchMap { id ->
        repo.getAll(id)
    }
    
    val folders: LiveData<List<WatchlistFolder>> = _userId.switchMap { id ->
        repo.getFolders(id)
    }

    fun add(movie: com.example.watchive.data.remote.model.Movie) = viewModelScope.launch {
        val id = _userId.value ?: -1
        if (id != -1) repo.add(movie, id)
    }

    fun remove(movieId: Int) = viewModelScope.launch {
        val id = _userId.value ?: -1
        if (id != -1) repo.remove(movieId, id)
    }

    suspend fun exists(movieId: Int): Boolean {
        val id = _userId.value ?: -1
        return if (id != -1) repo.exists(movieId, id) else false
    }
    
    fun getUserId(): Int = _userId.value ?: -1
}
