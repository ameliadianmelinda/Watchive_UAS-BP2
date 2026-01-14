package com.example.watchive.ui.detail

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.watchive.R
import com.example.watchive.data.local.AppDatabase
import com.example.watchive.data.local.FolderMovieJoin
import com.example.watchive.data.local.WatchlistRepository
import com.example.watchive.data.remote.RetrofitClient
import com.example.watchive.data.remote.model.Movie
import com.example.watchive.databinding.BottomSheetAddToPlaylistBinding
import com.example.watchive.databinding.FragmentMovieDetailBinding
import com.example.watchive.ui.watchlist.CreateFolderActivity
import com.example.watchive.ui.watchlist.FolderSelectionAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var actorAdapter: ActorAdapter
    private lateinit var watchlistRepo: WatchlistRepository
    private var currentMovie: Movie? = null
    private var isSavedInAnyFolder = false
    private var userId: Int = -1

    companion object {
        const val ARG_MOVIE_ID = "movieId"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }

        watchlistRepo = WatchlistRepository.create(requireContext())
        
        val sharedPref = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPref.getInt("user_id", -1)

        actorAdapter = ActorAdapter(emptyList())
        binding.rvActors.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = actorAdapter
        }

        val movieId = arguments?.getInt(ARG_MOVIE_ID)
        movieId?.let { id ->
            lifecycleScope.launch {
                val response = withContext(Dispatchers.IO) {
                    try { RetrofitClient.instance.getMovieDetails(id) } catch (e: Exception) { null }
                }

                if (response != null && response.isSuccessful) {
                    val movieFull = response.body()
                    movieFull?.let { mf ->
                        currentMovie = mf
                        bindBasicFields(mf)
                        mf.credits?.cast?.let { actorAdapter.updateData(it) }

                        checkIfSavedInAnyFolder(mf.id)

                        binding.btnBookmark.setOnClickListener {
                            showAddToPlaylistBottomSheet(mf)
                        }
                    }
                }
            }
        }
    }

    private suspend fun checkIfSavedInAnyFolder(movieId: Int) {
        val db = AppDatabase.getInstance(requireContext())
        val exists = withContext(Dispatchers.IO) { 
            // Cek apakah film ini sudah ada di watchlist (yang sekarang hanya berisi film di folder)
            watchlistRepo.exists(movieId, userId) 
        }
        isSavedInAnyFolder = exists
        withContext(Dispatchers.Main) {
            updateBookmarkIcon()
        }
    }

    private fun updateBookmarkIcon() {
        if (isSavedInAnyFolder) {
            binding.btnBookmark.setImageResource(R.drawable.ic_bookmark_filled)
            binding.btnBookmark.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.purple_medium))
        } else {
            binding.btnBookmark.setImageResource(R.drawable.ic_bookmark_outline)
            binding.btnBookmark.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }

    private fun showAddToPlaylistBottomSheet(movie: Movie) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val sheetBinding = BottomSheetAddToPlaylistBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(sheetBinding.root)

        sheetBinding.btnNewPlaylist.setOnClickListener {
            bottomSheetDialog.dismiss()
            val intent = Intent(requireContext(), CreateFolderActivity::class.java)
            startActivity(intent)
        }

        val db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            val folders = withContext(Dispatchers.IO) { db.folderDao().getAllFoldersStatic(userId) }
            if (folders.isNotEmpty()) {
                sheetBinding.rvFoldersList.layoutManager = LinearLayoutManager(context)
                sheetBinding.rvFoldersList.adapter = FolderSelectionAdapter(folders) { selectedFolder ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        // Simpan film ke database watchlist dulu (syarat FK)
                        db.watchlistDao().insert(com.example.watchive.data.local.WatchlistMovie.fromMovie(movie, userId))
                        // Masukkan ke folder
                        db.folderDao().addMovieToFolder(FolderMovieJoin(selectedFolder.id, movie.id))
                        
                        isSavedInAnyFolder = true
                        withContext(Dispatchers.Main) {
                            updateBookmarkIcon()
                            Toast.makeText(context, "Added to ${selectedFolder.title}", Toast.LENGTH_SHORT).show()
                            bottomSheetDialog.dismiss()
                        }
                    }
                }
            }
        }

        bottomSheetDialog.show()
    }

    private fun bindBasicFields(movie: Movie) {
        binding.tvTitle.text = movie.title
        binding.tvSynopsis.text = movie.overview
        val year = movie.releaseDate?.split("-")?.get(0) ?: "N/A"
        binding.tvYearDirector.text = getString(R.string.year_director_format, year)
        binding.ratingBar.rating = (movie.voteAverage / 2).toFloat()

        val imageUrl = movie.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
        binding.imgPoster.load(imageUrl) {
            crossfade(true)
            placeholder(R.drawable.login_bg_gradient)
            transformations(RoundedCornersTransformation(16f))
        }

        val genres = movie.genres?.map { it.name } ?: emptyList()
        binding.chipGroupGenres.removeAllViews()
        for (g in genres) {
            val chip = com.google.android.material.chip.Chip(requireContext())
            chip.text = g
            chip.isClickable = false
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_dark))
            binding.chipGroupGenres.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
