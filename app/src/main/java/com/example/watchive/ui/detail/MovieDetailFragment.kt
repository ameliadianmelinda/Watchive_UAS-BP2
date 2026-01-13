package com.example.watchive.ui.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.example.watchive.databinding.FragmentMovieDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var actorAdapter: ActorAdapter
    private lateinit var watchlistRepo: WatchlistRepository

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
        var currentMovie: Movie? = null
        var isSaved = false

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

                        val exists = withContext(Dispatchers.IO) { watchlistRepo.exists(mf.id) }
                        isSaved = exists
                        binding.btnBookmark.setImageResource(if (isSaved) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_outline)

                        binding.btnBookmark.setOnClickListener {
                            CoroutineScope(Dispatchers.IO).launch {
                                if (isSaved) {
                                    watchlistRepo.remove(mf.id)
                                    isSaved = false
                                } else {
                                    watchlistRepo.add(mf)
                                    isSaved = true
                                }
                                withContext(Dispatchers.Main) { 
                                    binding.btnBookmark.setImageResource(if (isSaved) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_outline) 
                                }
                            }
                        }
                    }
                }
            }
        }

        // Setup "Add to Folder" button (Assuming there's a button, or use bookmark long click)
        binding.btnBookmark.setOnLongClickListener {
            currentMovie?.let { showAddToFolderDialog(it.id) }
            true
        }
    }

    private fun showAddToFolderDialog(movieId: Int) {
        val db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            val folders = withContext(Dispatchers.IO) { db.folderDao().getAllFoldersStatic() }
            if (folders.isNullOrEmpty()) {
                Toast.makeText(context, "Belum ada folder. Buat folder di halaman Watchlist.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val folderNames = folders.map { it.title }.toTypedArray()
            AlertDialog.Builder(requireContext())
                .setTitle("Tambah ke Folder")
                .setItems(folderNames) { _, which ->
                    val selectedFolder = folders[which]
                    lifecycleScope.launch(Dispatchers.IO) {
                        db.folderDao().addMovieToFolder(FolderMovieJoin(selectedFolder.id, movieId))
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Berhasil ditambahkan ke ${selectedFolder.title}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .show()
        }
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
