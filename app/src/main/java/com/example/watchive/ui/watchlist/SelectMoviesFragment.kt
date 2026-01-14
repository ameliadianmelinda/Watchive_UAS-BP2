package com.example.watchive.ui.watchlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchive.data.local.AppDatabase
import com.example.watchive.data.local.FolderMovieJoin
import com.example.watchive.data.local.WatchlistMovie
import com.example.watchive.data.remote.RetrofitClient
import com.example.watchive.databinding.FragmentSelectMoviesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectMoviesFragment : Fragment() {

    private var _binding: FragmentSelectMoviesBinding? = null
    private val binding get() = _binding!!

    private var folderId: Int = -1
<<<<<<< HEAD
    // Mengubah Set menjadi WatchlistMovie agar sesuai dengan tipe data adapter
    private val selectedMovies = mutableSetOf<WatchlistMovie>()
=======
    private val selectedMovies = mutableSetOf<Movie>()
    private var searchJob: Job? = null
>>>>>>> f64f4956950dbb7c1aa94ea6d268a10e174579de

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
        fetchInitialMovies()
    }

    private fun fetchInitialMovies() {
        searchMovies("") // Initial fetch (popular movies)
    }

    private fun searchMovies(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            if (query.isNotEmpty()) delay(500) // Debounce for typing
            
            try {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvSelectMovies.alpha = 0.5f // Visual feedback for loading

                val response = withContext(Dispatchers.IO) {
                    if (query.isEmpty()) {
                        RetrofitClient.instance.getPopularMovies(page = 1)
                    } else {
                        RetrofitClient.instance.searchMovies(query = query, page = 1)
                    }
                }

                if (response.isSuccessful && _binding != null) {
                    val apiMovies = response.body()?.movies ?: emptyList()
                    updateAdapter(apiMovies)
                }
            } catch (e: Exception) {
                if (_binding != null) Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    binding.rvSelectMovies.alpha = 1.0f
                }
            }
        }
    }

    private fun updateAdapter(movies: List<Movie>) {
        val adapter = SelectMoviesAdapter(movies) { movie, isSelected ->
            if (isSelected) selectedMovies.add(movie) else selectedMovies.remove(movie)
        }
        binding.rvSelectMovies.layoutManager = LinearLayoutManager(context)
        binding.rvSelectMovies.adapter = adapter
    }

    private fun setupRecyclerView() {
<<<<<<< HEAD
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getPopularMovies(page = 1)
                }

                if (response.isSuccessful) {
                    val apiMovies = response.body()?.movies ?: emptyList()
                    
                    // Konversi Movie (API) ke WatchlistMovie (Local) agar bisa diterima Adapter
                    val watchlistMovies = apiMovies.map { WatchlistMovie.fromMovie(it) }
                    
                    val adapter = SelectMoviesAdapter(watchlistMovies) { movie, isSelected ->
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
=======
        // Initial empty state
        binding.rvSelectMovies.layoutManager = LinearLayoutManager(context)
>>>>>>> f64f4956950dbb7c1aa94ea6d268a10e174579de
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Search text change listener
        binding.etSearchMovies.addTextChangedListener { text ->
            searchMovies(text.toString().trim())
        }

        // Search action listener (Enter key)
        binding.etSearchMovies.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchMovies(binding.etSearchMovies.text.toString().trim())
                true
            } else false
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

            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getInstance(requireContext())
<<<<<<< HEAD
                selectedMovies.forEach { watchlistMovie ->
                    // 1. Simpan/Update film ke tabel watchlist (Local Database)
                    db.watchlistDao().insert(watchlistMovie)
                    
                    // 2. Hubungkan film tersebut ke folder yang dipilih
                    db.folderDao().addMovieToFolder(FolderMovieJoin(folderId, watchlistMovie.id))
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "${selectedMovies.size} film ditambahkan ke folder", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
=======
                val moviesToInsert = selectedMovies.map { WatchlistMovie.fromMovie(it, userId) }
                val folderJoins = selectedMovies.map { FolderMovieJoin(folderId, it.id) }

                try {
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
>>>>>>> f64f4956950dbb7c1aa94ea6d268a10e174579de
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
