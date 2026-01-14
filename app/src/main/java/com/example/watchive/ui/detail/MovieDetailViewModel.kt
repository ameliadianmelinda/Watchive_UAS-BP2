package com.example.watchive.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchive.data.remote.RetrofitClient
import com.example.watchive.data.remote.model.Movie
import kotlinx.coroutines.launch

class MovieDetailViewModel : ViewModel() {

    private val _movieDetails = MutableLiveData<Movie?>()
    
    fun getMovieDetails(movieId: Int): LiveData<Movie?> {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getMovieDetails(movieId)
                if (response.isSuccessful) {
                    _movieDetails.postValue(response.body())
                }
            } catch (e: Exception) {
                _movieDetails.postValue(null)
            }
        }
        return _movieDetails
    }
}
