package com.example.watchive.ui.watchlist

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchive.R
import com.example.watchive.data.local.AppDatabase
import com.example.watchive.data.local.FolderMovieJoin
import com.example.watchive.data.local.WatchlistMovie
import com.example.watchive.data.remote.RetrofitClient
import com.example.watchive.data.remote.model.Movie
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
    private val selectedMovies = mutableSetOf<Movie>()
    private var searchJob: Job? = null

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
        applyTheme()
    }

    private fun applyTheme() {
        val sharedPref = requireActivity().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("isDarkMode", true)
        
        if (!isDarkMode) {
            val context = requireContext()
            val brandColor = ContextCompat.getColor(context, R.color.brand)
            val darkPurple = ContextCompat.getColor(context, R.color.purple_dark)
            val whiteTrans = Color.parseColor("#80FFFFFF")

            binding.root.setBackgroundColor(brandColor)
            binding.tvTitleHeader.setTextColor(darkPurple)
            binding.btnBack.imageTintList = ColorStateList.valueOf(darkPurple)
            binding.btnDone.setTextColor(darkPurple)

            binding.searchContainer.backgroundTintList = ColorStateList.valueOf(whiteTrans)
            binding.etSearchMovies.setTextColor(darkPurple)
            binding.etSearchMovies.setHintTextColor(darkPurple)
            binding.ivSearchIcon.imageTintList = ColorStateList.valueOf(darkPurple)
            
            updateAllTextViewsColor(binding.root as ViewGroup, darkPurple)
        }
    }

    private fun updateAllTextViewsColor(viewGroup: ViewGroup, color: Int) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is TextView) {
                child.setTextColor(color)
            } else if (child is ViewGroup) {
                updateAllTextViewsColor(child, color)
            }
        }
    }

    private fun fetchInitialMovies() {
        searchMovies("")
    }

    private fun searchMovies(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            if (query.isNotEmpty()) delay(500)
            
            try {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvSelectMovies.alpha = 0.5f

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
        binding.rvSelectMovies.layoutManager = LinearLayoutManager(context)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.etSearchMovies.addTextChangedListener { text ->
            searchMovies(text.toString().trim())
        }

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

            lifecycleScope.launch {
                try {
                    val db = AppDatabase.getInstance(requireContext())
                    
                    withContext(Dispatchers.IO) {
                        // 1. Simpan semua film terpilih ke tabel 'watchlist' (induk)
                        val moviesToInsert = selectedMovies.map { WatchlistMovie.fromMovie(it, userId) }
                        db.watchlistDao().insertAll(moviesToInsert)
                        
                        // 2. Hubungkan film-film tersebut ke folder ini di tabel 'folder_movie_join'
                        val folderJoins = selectedMovies.map { FolderMovieJoin(folderId, it.id) }
                        db.folderDao().addMoviesToFolder(folderJoins)
                    }

                    Toast.makeText(context, "${selectedMovies.size} film ditambahkan!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                    
                } catch (e: Exception) {
                    Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
