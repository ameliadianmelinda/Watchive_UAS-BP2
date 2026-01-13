package com.example.watchive.ui.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchive.data.local.AppDatabase
import com.example.watchive.data.local.FolderMovieJoin
import com.example.watchive.data.local.WatchlistMovie
import com.example.watchive.data.remote.RetrofitClient
import com.example.watchive.data.remote.model.Movie
import com.example.watchive.databinding.FragmentSelectMoviesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectMoviesFragment : Fragment() {

    private var _binding: FragmentSelectMoviesBinding? = null
    private val binding get() = _binding!!

    private var folderId: Int = -1
    private val selectedMovies = mutableSetOf<Movie>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        folderId = arguments?.getInt("folderId") ?: -1
        if (folderId == -1) {
            findNavController().navigateUp()
            return
        }

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        lifecycleScope.launch {
            try {
                // Ambil film populer dari API (sebagai perwakilan "semua film")
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getPopularMovies(page = 1)
                }

                if (response.isSuccessful) {
                    val apiMovies = response.body()?.movies ?: emptyList()
                    
                    // Kita perlu mengonversi Movie (API) ke WatchlistMovie (Local) untuk adapter jika diperlukan, 
                    // atau ubah adapter agar menerima Movie. Mari kita gunakan data class Movie saja.
                    
                    val adapter = SelectMoviesAdapter(apiMovies) { movie, isSelected ->
                        if (isSelected) selectedMovies.add(movie) else selectedMovies.remove(movie)
                    }
                    
                    binding.rvSelectMovies.layoutManager = LinearLayoutManager(context)
                    binding.rvSelectMovies.adapter = adapter
                } else {
                    Toast.makeText(context, "Gagal memuat daftar film", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnDone.setOnClickListener {
            if (selectedMovies.isEmpty()) {
                Toast.makeText(context, "Pilih minimal satu film", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getInstance(requireContext())
                selectedMovies.forEach { movie ->
                    // 1. Pastikan film ada di tabel watchlist (karena foreign key)
                    db.watchlistDao().insert(WatchlistMovie.fromMovie(movie))
                    
                    // 2. Tambahkan ke folder
                    db.folderDao().addMovieToFolder(FolderMovieJoin(folderId, movie.id))
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "${selectedMovies.size} film ditambahkan ke folder", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
