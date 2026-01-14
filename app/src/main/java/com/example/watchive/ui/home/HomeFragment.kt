package com.example.watchive.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.watchive.R
import com.example.watchive.data.remote.model.Movie
import com.example.watchive.databinding.FragmentHomeBinding
import com.example.watchive.ui.adapter.MovieAdapter
<<<<<<< HEAD
=======
import com.example.watchive.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
>>>>>>> f64f4956950dbb7c1aa94ea6d268a10e174579de

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    
    private var recommendationsAdapter: MovieAdapter? = null
    private var newReleasesAdapter: MovieAdapter? = null
    private var searchResultsAdapter: MovieAdapter? = null
    
    private var searchJob: Job? = null

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
<<<<<<< HEAD
    }

    private fun setupRecyclerViews() {
        // Perbaikan cara inisialisasi MovieAdapter (ListAdapter tidak butuh list di constructor)
        recommendationsAdapter = MovieAdapter(null) { movie ->
            onMovieClicked(movie)
        }
=======
        handleArgumentsForScroll()
        setupSearch()
    }

    private fun setupSearch() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    binding.homeContentLayout.visibility = View.GONE
                    binding.searchResultsLayout.visibility = View.VISIBLE
                    performSearch(query)
                } else {
                    binding.homeContentLayout.visibility = View.VISIBLE
                    binding.searchResultsLayout.visibility = View.GONE
                    searchJob?.cancel()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                true
            } else false
        }
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(500) // Debounce
            try {
                binding.progressBar.visibility = View.VISIBLE
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.searchMovies(query = query)
                }
                if (response.isSuccessful && isAdded) {
                    val movies = response.body()?.movies ?: emptyList()
                    searchResultsAdapter?.submitList(movies)
                }
            } catch (e: Exception) {
                if (isAdded) Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                if (isAdded) binding.progressBar.visibility = View.GONE
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
>>>>>>> f64f4956950dbb7c1aa94ea6d268a10e174579de
        binding.rvRecommendations.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendationsAdapter
        }

<<<<<<< HEAD
        newReleasesAdapter = MovieAdapter(null) { movie ->
            onMovieClicked(movie)
        }
=======
        newReleasesAdapter = MovieAdapter { movie -> onMovieClicked(movie) }
>>>>>>> f64f4956950dbb7c1aa94ea6d268a10e174579de
        binding.rvNewReleases.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = newReleasesAdapter
        }

        searchResultsAdapter = MovieAdapter { movie -> onMovieClicked(movie) }
        binding.rvHomeSearchResults.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchResultsAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
<<<<<<< HEAD
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
=======
            if (isLoading) {
                showShimmer(true)
            } else {
                // Dihapus delay 3 detik agar data muncul lebih responsif
                if (_binding != null) {
                    showShimmer(false)
                }
            }
>>>>>>> f64f4956950dbb7c1aa94ea6d268a10e174579de
        }

        viewModel.popularMovies.observe(viewLifecycleOwner) { movies ->
            if (!movies.isNullOrEmpty()) {
                setupFeaturedMovie(movies[0])
            }
        }

        viewModel.topRatedMovies.observe(viewLifecycleOwner) { movies ->
<<<<<<< HEAD
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
=======
            movies?.let { recommendationsAdapter?.submitList(it) }
        }

        viewModel.nowPlayingMovies.observe(viewLifecycleOwner) { movies ->
            movies?.let { newReleasesAdapter?.submitList(it) }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty() && isAdded) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
>>>>>>> f64f4956950dbb7c1aa94ea6d268a10e174579de
            }
        }
    }

    private fun showShimmer(show: Boolean) {
        if (show) {
            binding.shimmerFeatured.startShimmer()
            binding.shimmerRecommendations.startShimmer()
            binding.shimmerNewReleases.startShimmer()
            binding.shimmerFeatured.visibility = View.VISIBLE
            binding.shimmerRecommendations.visibility = View.VISIBLE
            binding.shimmerNewReleases.visibility = View.VISIBLE
        } else {
            binding.shimmerFeatured.stopShimmer()
            binding.shimmerRecommendations.stopShimmer()
            binding.shimmerNewReleases.stopShimmer()
            binding.shimmerFeatured.hideShimmer()
            binding.shimmerRecommendations.hideShimmer()
            binding.shimmerNewReleases.hideShimmer()
            binding.progressBar.visibility = View.GONE
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
<<<<<<< HEAD
=======

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
>>>>>>> f64f4956950dbb7c1aa94ea6d268a10e174579de
        
        binding.featuredMovieCard.setOnClickListener {
            onMovieClicked(movie)
        }
    }

    private fun onMovieClicked(movie: Movie) {
<<<<<<< HEAD
        Toast.makeText(context, "Clicked: ${movie.title}", Toast.LENGTH_SHORT).show()
=======
        if (isAdded) {
            val bundle = Bundle().apply { putInt("movieId", movie.id) }
            findNavController().navigate(resId = R.id.movieDetailFragment, args = bundle)
        }
>>>>>>> f64f4956950dbb7c1aa94ea6d268a10e174579de
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recommendationsAdapter = null
        newReleasesAdapter = null
        searchResultsAdapter = null
        _binding = null
    }
}
