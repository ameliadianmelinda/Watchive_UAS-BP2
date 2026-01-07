package com.example.watchive.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchive.data.remote.RetrofitClient
import com.example.watchive.data.remote.model.Movie
import kotlinx.coroutines.launch

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
                val topRatedResponse = RetrofitClient.instance.getTopRatedMovies()
                val nowPlayingResponse = RetrofitClient.instance.getNowPlayingMovies()

                if (popularResponse.isSuccessful) _popularMovies.postValue(popularResponse.body()?.movies)
                if (topRatedResponse.isSuccessful) _topRatedMovies.postValue(topRatedResponse.body()?.movies)
                if (nowPlayingResponse.isSuccessful) _nowPlayingMovies.postValue(nowPlayingResponse.body()?.movies)

            } catch (e: Exception) {
                _errorMessage.postValue("An error occurred: ${e.message}")
            }
            _isLoading.postValue(false)
        }
    }
}
