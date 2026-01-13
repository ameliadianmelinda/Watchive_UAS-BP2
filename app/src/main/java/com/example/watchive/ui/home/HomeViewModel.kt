package com.example.watchive.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchive.data.remote.RetrofitClient
import com.example.watchive.data.remote.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val _popularMovies = MutableLiveData<List<Movie>>()
    val popularMovies: LiveData<List<Movie>> = _popularMovies

    private val _topRatedMovies = MutableLiveData<List<Movie>>()
    val topRatedMovies: LiveData<List<Movie>> = _topRatedMovies

    private val _nowPlayingMovies = MutableLiveData<List<Movie>>()
    val nowPlayingMovies: LiveData<List<Movie>> = _nowPlayingMovies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        fetchAllMovies()
    }

    private fun fetchAllMovies() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val popularResponse = RetrofitClient.instance.getPopularMovies()
                val topRatedResponse = RetrofitClient.instance.getTopRatedMovies(page = 1)
                val nowPlayingResponse = RetrofitClient.instance.getNowPlayingMovies(page = 1)

                if (popularResponse.isSuccessful) _popularMovies.postValue(popularResponse.body()?.movies)
                if (topRatedResponse.isSuccessful) _topRatedMovies.postValue(topRatedResponse.body()?.movies)
                if (nowPlayingResponse.isSuccessful) _nowPlayingMovies.postValue(nowPlayingResponse.body()?.movies)

                // init pagination trackers
                topRatedPage = 1
                topRatedTotalPages = topRatedResponse.body()?.totalPages ?: 1
                nowPlayingPage = 1
                nowPlayingTotalPages = nowPlayingResponse.body()?.totalPages ?: 1

            } catch (e: Exception) {
                _errorMessage.postValue("An error occurred: ${e.message}")
            }
            _isLoading.postValue(false)
        }
    }

    // Pagination state
    private var topRatedPage = 1
    private var topRatedTotalPages = 1

    private var nowPlayingPage = 1
    private var nowPlayingTotalPages = 1

    fun loadMoreTopRated() {
        if (topRatedPage >= topRatedTotalPages) return
        viewModelScope.launch {
            val next = topRatedPage + 1
            val resp = withContext(Dispatchers.IO) { try { RetrofitClient.instance.getTopRatedMovies(page = next) } catch (e: Exception) { null } }
            if (resp != null && resp.isSuccessful) {
                val newMovies = resp.body()?.movies ?: emptyList()
                val current = _topRatedMovies.value ?: emptyList()
                _topRatedMovies.postValue(current + newMovies)
                topRatedPage = next
                topRatedTotalPages = resp.body()?.totalPages ?: topRatedTotalPages
            }
        }
    }

    fun loadMoreNowPlaying() {
        if (nowPlayingPage >= nowPlayingTotalPages) return
        viewModelScope.launch {
            val next = nowPlayingPage + 1
            val resp = withContext(Dispatchers.IO) { try { RetrofitClient.instance.getNowPlayingMovies(page = next) } catch (e: Exception) { null } }
            if (resp != null && resp.isSuccessful) {
                val newMovies = resp.body()?.movies ?: emptyList()
                val current = _nowPlayingMovies.value ?: emptyList()
                _nowPlayingMovies.postValue(current + newMovies)
                nowPlayingPage = next
                nowPlayingTotalPages = resp.body()?.totalPages ?: nowPlayingTotalPages
            }
        }
    }
}
