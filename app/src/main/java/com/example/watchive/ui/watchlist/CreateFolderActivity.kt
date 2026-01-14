package com.example.watchive.ui.watchlist

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.watchive.R
import com.example.watchive.data.local.AppDatabase
import com.example.watchive.data.local.FolderMovieJoin
import com.example.watchive.data.local.WatchlistFolder
import com.example.watchive.data.local.WatchlistMovie
import com.example.watchive.data.remote.RetrofitClient
import com.example.watchive.databinding.ActivityCreateFolderBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateFolderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateFolderBinding
    private var movieIdFromIntent: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tangkap movieId jika ada (dari Bookmark Bottom Sheet)
        movieIdFromIntent = intent.getIntOfDefault("movieId", -1)

        applyTheme()

        binding.btnBack.setOnClickListener { finish() }

        binding.btnSaveFolder.setOnClickListener {
            val title = binding.etFolderTitle.text.toString().trim()
            val description = binding.etFolderDescription.text.toString().trim()

            if (title.isNotEmpty()) {
                saveFolder(title, description)
            } else {
                Toast.makeText(this, "Judul folder tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveFolder(title: String, description: String) {
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(this, "Sesi berakhir, silakan login kembali", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val db = AppDatabase.getInstance(this@CreateFolderActivity)
                val folder = WatchlistFolder(userId = userId, title = title, description = description)
                
                // 1. Simpan folder dan dapatkan ID-nya
                val newFolderId = db.folderDao().insertFolder(folder).toInt()

                // 2. JIKA ada movieId, otomatis masukkan film ke folder baru ini
                if (movieIdFromIntent != -1) {
                    saveMovieToNewFolder(newFolderId, userId)
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateFolderActivity, "Folder '$title' dibuat!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CreateFolderActivity, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun saveMovieToNewFolder(folderId: Int, userId: Int) {
        try {
            val db = AppDatabase.getInstance(this)
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.instance.getMovieDetails(movieIdFromIntent)
            }

            if (response.isSuccessful) {
                response.body()?.let { movieDetail ->
                    db.watchlistDao().insert(WatchlistMovie.fromMovie(movieDetail, userId))
                    db.folderDao().addMovieToFolder(FolderMovieJoin(folderId, movieIdFromIntent))
                }
            }
        } catch (e: Exception) {
            // Log error jika perlu
        }
    }

    private fun android.content.Intent.getIntOfDefault(key: String, defaultValue: Int): Int {
        return if (hasExtra(key)) getIntExtra(key, defaultValue) else defaultValue
    }

    private fun applyTheme() {
        val sharedPref = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("isDarkMode", true)
        if (!isDarkMode) {
            val brandColor = ContextCompat.getColor(this, R.color.brand)
            val darkPurple = ContextCompat.getColor(this, R.color.purple_dark)
            val whiteTrans = Color.parseColor("#80FFFFFF")

            binding.root.setBackgroundColor(brandColor)
            binding.tvTitleHeader.setTextColor(darkPurple)
            binding.btnBack.imageTintList = ColorStateList.valueOf(darkPurple)
            binding.btnBack.backgroundTintList = ColorStateList.valueOf(whiteTrans)
            binding.etFolderTitle.setTextColor(darkPurple)
            binding.etFolderTitle.setHintTextColor(Color.parseColor("#8A320174"))
            binding.etFolderDescription.backgroundTintList = ColorStateList.valueOf(whiteTrans)
            binding.etFolderDescription.setTextColor(darkPurple)
            binding.etFolderDescription.setHintTextColor(Color.parseColor("#8A320174"))
            binding.btnSaveFolder.backgroundTintList = ColorStateList.valueOf(darkPurple)
            
            updateAllTextViewsColor(binding.root as ViewGroup, darkPurple)
            binding.btnSaveFolder.setTextColor(Color.WHITE)
        }
    }

    private fun updateAllTextViewsColor(viewGroup: ViewGroup, color: Int) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is TextView && child !is Button) {
                child.setTextColor(color)
            } else if (child is ViewGroup) {
                updateAllTextViewsColor(child, color)
            }
        }
    }
}
