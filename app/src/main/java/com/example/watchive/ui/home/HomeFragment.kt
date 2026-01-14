package com.example.watchive.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.watchive.R
import com.example.watchive.data.remote.model.Movie
import com.example.watchive.databinding.FragmentHomeBinding
import com.example.watchive.ui.adapter.MovieAdapter
import com.example.watchive.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    
    private var recommendationsAdapter: MovieAdapter? = null
    private var newReleasesAdapter: MovieAdapter? = null

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
        handleArgumentsForScroll()
        
        // Setup search bar click
        binding.searchBar.isFocusable = false
        binding.searchBar.setOnClickListener {
            if (isAdded) {
                findNavController().navigate(resId = R.id.searchFragment)
            }
        }
    }

    private fun handleArgumentsForScroll() {
        val shouldScroll = arguments?.getBoolean("scroll_to_top") ?: false
        if (shouldScroll) {
            binding.scrollHome.post {
                binding.scrollHome.smoothScrollTo(0, 0)
            }
        }
    }

    private fun setupRecyclerViews() {
        recommendationsAdapter = MovieAdapter { movie -> onMovieClicked(movie) }
        binding.rvRecommendations.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendationsAdapter
        }

        newReleasesAdapter = MovieAdapter { movie -> onMovieClicked(movie) }
        binding.rvNewReleases.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = newReleasesAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.shimmerFeatured.startShimmer()
                binding.shimmerRecommendations.startShimmer()
                binding.shimmerNewReleases.startShimmer()
            } else {
                binding.shimmerFeatured.apply { stopShimmer(); visibility = View.VISIBLE }
                binding.shimmerRecommendations.apply { stopShimmer(); visibility = View.VISIBLE }
                binding.shimmerNewReleases.apply { stopShimmer(); visibility = View.VISIBLE }
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.popularMovies.observe(viewLifecycleOwner) { movies ->
            if (!movies.isNullOrEmpty()) {
                setupFeaturedMovie(movies[0])
            }
        }

        viewModel.topRatedMovies.observe(viewLifecycleOwner) { movies ->
            movies?.let { recommendationsAdapter?.submitList(it) }
        }

        viewModel.nowPlayingMovies.observe(viewLifecycleOwner) { movies ->
            movies?.let { newReleasesAdapter?.submitList(it) }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty() && isAdded) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupFeaturedMovie(movie: Movie) {
        binding.featuredMovieTitle.text = movie.title
        binding.featuredMovieRating.rating = (movie.voteAverage / 2).toFloat()
        
        val genreName = when (movie.genreIds?.firstOrNull()) {
            28 -> "Action"
            12 -> "Adventure"
            16 -> "Animation"
            35 -> "Comedy"
            80 -> "Crime"
            18 -> "Drama"
            10751 -> "Family"
            14 -> "Fantasy"
            27 -> "Horror"
            10749 -> "Romance"
            878 -> "Sci-Fi"
            53 -> "Thriller"
            else -> "Genre"
        }
        binding.featuredMovieGenre.text = genreName
        
        val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
        binding.featuredMoviePoster.load(imageUrl) {
            crossfade(true)
            placeholder(R.drawable.login_bg_gradient)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resp = withContext(Dispatchers.IO) { 
                    RetrofitClient.instance.getMovieDetails(movie.id) 
                }
                if (resp.isSuccessful && isAdded) {
                    val details = resp.body()
                    val director = details?.credits?.crew?.find { it.job?.equals("Director", true) == true }?.name
                    binding.featuredMovieDirector.text = director ?: ""
                }
            } catch (e: Exception) { /* Ignore */ }
        }
        
        binding.featuredMovieCard.setOnClickListener {
            onMovieClicked(movie)
        }
    }

    private fun onMovieClicked(movie: Movie) {
        if (isAdded) {
            val bundle = Bundle().apply { putInt("movieId", movie.id) }
            findNavController().navigate(resId = R.id.movieDetailFragment, args = bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recommendationsAdapter = null
        newReleasesAdapter = null
        _binding = null
    }
}
