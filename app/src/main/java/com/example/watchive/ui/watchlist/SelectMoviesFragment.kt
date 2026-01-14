package com.example.watchive.ui.watchlist

import android.content.Context
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
                binding.rvSelectMovies.visibility = View.GONE
                // Ambil film populer dari API
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getPopularMovies(page = 1)
                }

                if (response.isSuccessful) {
                    val apiMovies = response.body()?.movies ?: emptyList()
                    
                    val adapter = SelectMoviesAdapter(apiMovies) { movie, isSelected ->
                        if (isSelected) selectedMovies.add(movie) else selectedMovies.remove(movie)
                    }
                    
                    binding.rvSelectMovies.layoutManager = LinearLayoutManager(context)
                    binding.rvSelectMovies.adapter = adapter
                    binding.rvSelectMovies.visibility = View.VISIBLE
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

            val sharedPref = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = sharedPref.getInt("user_id", -1)

            if (userId == -1) {
                Toast.makeText(context, "Sesi berakhir, silakan login kembali", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // OPTIMASI: Gunakan Batch Insert agar tidak ANR (Not Responding)
            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getInstance(requireContext())
                
                // 1. Siapkan data masal
                val moviesToInsert = selectedMovies.map { WatchlistMovie.fromMovie(it, userId) }
                val folderJoins = selectedMovies.map { FolderMovieJoin(folderId, it.id) }

                try {
                    // 2. Simpan sekaligus
                    db.watchlistDao().insertAll(moviesToInsert)
                    db.folderDao().addMoviesToFolder(folderJoins)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "${selectedMovies.size} film ditambahkan", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Gagal menyimpan: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
