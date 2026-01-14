package com.example.watchive.ui.watchlist

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.watchive.data.local.AppDatabase
import com.example.watchive.data.local.WatchlistFolder
import com.example.watchive.databinding.ActivityCreateFolderBinding
import kotlinx.coroutines.launch

class CreateFolderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateFolderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

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
        // Ambil userId dari SharedPreferences
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(this, "Sesi berakhir, silakan login kembali", Toast.LENGTH_SHORT).show()
            return
        }

        val folder = WatchlistFolder(userId = userId, title = title, description = description)
        val db = AppDatabase.getInstance(this)
        
        lifecycleScope.launch {
            db.folderDao().insertFolder(folder)
            Toast.makeText(this@CreateFolderActivity, "Folder '$title' dibuat!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
