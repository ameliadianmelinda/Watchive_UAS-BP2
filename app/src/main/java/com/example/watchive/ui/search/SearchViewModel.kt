package com.example.watchive.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchive.data.remote.RetrofitClient
import com.example.watchive.data.remote.model.Movie
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _searchResults = MutableLiveData<List<Movie>>()
    val searchResults: LiveData<List<Movie>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun searchMovies(query: String) {
        if (query.isEmpty()) return
        
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.searchMovies(query = query)
                if (response.isSuccessful) {
                    _searchResults.postValue(response.body()?.movies ?: emptyList())
                } else {
                    _error.postValue("Gagal mencari film")
                }
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
