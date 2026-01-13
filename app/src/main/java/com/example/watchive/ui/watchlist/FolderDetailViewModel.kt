package com.example.watchive.ui.watchlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.watchive.data.local.AppDatabase
import com.example.watchive.data.local.FolderMovieJoin
import com.example.watchive.data.local.WatchlistFolder
import com.example.watchive.data.local.WatchlistMovie
import kotlinx.coroutines.launch

class FolderDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val folderDao = AppDatabase.getInstance(application).folderDao()

    private val _folder = MutableLiveData<WatchlistFolder?>()
    val folder: LiveData<WatchlistFolder?> = _folder

    fun loadFolder(folderId: Int) {
        viewModelScope.launch {
            _folder.value = folderDao.getFolderById(folderId)
        }
    }

    fun getMoviesInFolder(folderId: Int): LiveData<List<WatchlistMovie>> {
        return folderDao.getMoviesInFolder(folderId)
    }

    fun updateFolder(id: Int, title: String, description: String) = viewModelScope.launch {
        folderDao.updateFolder(WatchlistFolder(id, title, description))
        loadFolder(id)
    }

    fun deleteFolder(folder: WatchlistFolder) = viewModelScope.launch {
        folderDao.deleteFolder(folder)
    }

    fun removeMovieFromFolder(folderId: Int, movieId: Int) = viewModelScope.launch {
        folderDao.removeMovieFromFolder(folderId, movieId)
    }
    
    fun addMovieToFolder(folderId: Int, movieId: Int) = viewModelScope.launch {
        folderDao.addMovieToFolder(FolderMovieJoin(folderId, movieId))
    }
}
