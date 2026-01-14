package com.example.watchive.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.watchive.R
import com.example.watchive.data.remote.model.Movie
import com.example.watchive.databinding.FragmentHomeBinding
import com.example.watchive.ui.adapter.MovieAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    
    private lateinit var recommendationsAdapter: MovieAdapter
    private lateinit var newReleasesAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        // Perbaikan cara inisialisasi MovieAdapter (ListAdapter tidak butuh list di constructor)
        recommendationsAdapter = MovieAdapter(null) { movie ->
            onMovieClicked(movie)
        }
        binding.rvRecommendations.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendationsAdapter
        }

        newReleasesAdapter = MovieAdapter(null) { movie ->
            onMovieClicked(movie)
        }
        binding.rvNewReleases.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = newReleasesAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.popularMovies.observe(viewLifecycleOwner) { movies ->
            if (!movies.isNullOrEmpty()) {
                setupFeaturedMovie(movies[0])
            }
        }

        viewModel.topRatedMovies.observe(viewLifecycleOwner) { movies ->
            movies?.let { recommendationsAdapter.submitList(it) }
        }

        viewModel.nowPlayingMovies.observe(viewLifecycleOwner) { movies ->
            movies?.let { newReleasesAdapter.submitList(it) }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let { 
                if (it.isNotEmpty()) {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupFeaturedMovie(movie: Movie) {
        binding.featuredMovieTitle.text = movie.title
        binding.featuredMovieRating.rating = (movie.voteAverage / 2).toFloat()
        
        val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
        binding.featuredMoviePoster.load(imageUrl) {
            crossfade(true)
            placeholder(R.drawable.login_bg_gradient)
        }
        
        binding.featuredMovieCard.setOnClickListener {
            onMovieClicked(movie)
        }
    }

    private fun onMovieClicked(movie: Movie) {
        Toast.makeText(context, "Clicked: ${movie.title}", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
