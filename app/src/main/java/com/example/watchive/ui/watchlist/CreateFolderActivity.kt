package com.example.watchive.ui.watchlist

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
        val folder = WatchlistFolder(title = title, description = description)
        val db = AppDatabase.getInstance(this)
        
        lifecycleScope.launch {
            db.folderDao().insertFolder(folder)
            Toast.makeText(this@CreateFolderActivity, "Folder '$title' dibuat!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
