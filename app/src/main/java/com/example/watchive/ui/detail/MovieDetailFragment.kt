package com.example.watchive.ui.detail

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.watchive.R
import com.example.watchive.databinding.FragmentMovieDetailBinding

class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieDetailViewModel by viewModels()
    private lateinit var actorAdapter: ActorAdapter

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

        val movieId = arguments?.getInt("movieId") ?: return
        
        setupRecyclerView()
        observeViewModel(movieId)
        setupListeners(movieId)
        applyTheme()
    }

    private fun applyTheme() {
        val sharedPref = requireActivity().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("isDarkMode", true)
        
        val context = requireContext()
        val brandColor = ContextCompat.getColor(context, R.color.brand)
        val darkPurple = ContextCompat.getColor(context, R.color.purple_dark)
        val whiteTrans = Color.parseColor("#80FFFFFF")

        if (!isDarkMode) {
            binding.root.setBackgroundColor(brandColor)
            updateAllTextViewsColor(binding.root as ViewGroup, darkPurple)
            
            binding.tvSynopsis.parent.let { card ->
                if (card is com.google.android.material.card.MaterialCardView) {
                    card.setCardBackgroundColor(whiteTrans)
                    card.cardElevation = 0f
                }
            }
            binding.tvSynopsis.setTextColor(darkPurple)

            binding.btnBack.imageTintList = ColorStateList.valueOf(darkPurple)
            binding.btnBookmark.imageTintList = ColorStateList.valueOf(darkPurple)
            binding.tvYearDirector.setTextColor(darkPurple)
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

    private fun setupRecyclerView() {
        actorAdapter = ActorAdapter(emptyList())
        binding.rvActors.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = actorAdapter
        }
    }

    private fun observeViewModel(movieId: Int) {
        viewModel.getMovieDetails(movieId).observe(viewLifecycleOwner) { movie ->
            movie?.let {
                binding.tvTitle.text = it.title
                binding.tvSynopsis.text = it.overview
                binding.ratingBar.rating = (it.voteAverage / 2).toFloat()
                
                val director = it.credits?.crew?.find { c -> c.job == "Director" }?.name
                val year = it.releaseDate?.take(4) ?: ""
                binding.tvYearDirector.text = "$year - Directed by ${director ?: "Unknown"}"

                binding.imgPoster.load("https://image.tmdb.org/t/p/w500${it.posterPath}") {
                    crossfade(true)
                }
                
                it.credits?.cast?.let { cast ->
                    actorAdapter.updateData(cast)
                }
            }
        }
    }

    private fun setupListeners(movieId: Int) {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnBookmark.setOnClickListener {
            // Memunculkan Bottom Sheet untuk memilih watchlist
            val bundle = Bundle().apply { 
                putInt("movieId", movieId)
                // Jika kamu butuh data movie lengkap, bisa dikirim juga di sini
            }
            findNavController().navigate(R.id.addToPlaylistBottomSheet, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
