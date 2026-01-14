package com.example.watchive.ui.watchlist

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchive.R
import com.example.watchive.data.local.AppDatabase
import com.example.watchive.data.local.FolderMovieJoin
import com.example.watchive.data.local.WatchlistMovie
import com.example.watchive.data.remote.RetrofitClient
import com.example.watchive.databinding.BottomSheetAddToPlaylistBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddToPlaylistBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddToPlaylistBinding? = null
    private val binding get() = _binding!!
    
    private var movieId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddToPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        movieId = arguments?.getInt("movieId") ?: -1
        if (movieId == -1) {
            dismiss()
            return
        }

        setupListeners()
        setupFoldersList()
        applyTheme()
    }

    private fun setupListeners() {
        binding.btnNewPlaylist.setOnClickListener {
            // KIRIM movieId agar saat folder dibuat, film otomatis masuk
            val intent = Intent(requireContext(), CreateFolderActivity::class.java).apply {
                putExtra("movieId", movieId)
            }
            startActivity(intent)
            dismiss()
        }
    }

    private fun applyTheme() {
        val sharedPref = requireActivity().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("isDarkMode", true)
        
        if (!isDarkMode) {
            val darkPurple = ContextCompat.getColor(requireContext(), R.color.purple_dark)
            binding.root.setBackgroundResource(R.color.brand)
            binding.tvTitle.setTextColor(darkPurple)
            binding.tvNewPlaylist.setTextColor(darkPurple)
            binding.divider.setBackgroundColor(darkPurple)
        }
    }

    private fun setupFoldersList() {
        val sharedPrefUser = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPrefUser.getInt("user_id", -1)

        if (userId != -1) {
            val db = AppDatabase.getInstance(requireContext())
            lifecycleScope.launch {
                val folders = db.folderDao().getAllFoldersStatic(userId)
                
                val adapter = FolderAdapter { folder ->
                    saveMovieToFolder(folder.id, userId)
                }
                
                binding.rvFoldersList.layoutManager = LinearLayoutManager(context)
                binding.rvFoldersList.adapter = adapter
                adapter.submitList(folders)
            }
        }
    }

    private fun saveMovieToFolder(folderId: Int, userId: Int) {
        lifecycleScope.launch {
            try {
                val db = AppDatabase.getInstance(requireContext())
                
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getMovieDetails(movieId)
                }

                if (response.isSuccessful) {
                    val movieDetail = response.body()
                    movieDetail?.let {
                        db.watchlistDao().insert(WatchlistMovie.fromMovie(it, userId))
                        db.folderDao().addMovieToFolder(FolderMovieJoin(folderId, movieId))
                        
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Gagal menyimpan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
