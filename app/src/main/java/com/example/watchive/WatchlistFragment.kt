package com.example.watchive

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchive.databinding.FragmentWatchlistBinding
import com.example.watchive.ui.adapter.MovieAdapter
import com.example.watchive.ui.watchlist.CreateFolderActivity
import com.example.watchive.ui.watchlist.FolderAdapter
import com.example.watchive.ui.watchlist.WatchlistViewModel

class WatchlistFragment : Fragment() {

    private var _binding: FragmentWatchlistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WatchlistViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var folderAdapter: FolderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        observeViewModel()
        setupListeners()
    }

    private fun setupRecyclerViews() {
        // Setup Movies Adapter
        movieAdapter = MovieAdapter { movie ->
            val bundle = Bundle().apply { putInt("movieId", movie.id) }
            findNavController().navigate(R.id.movieDetailFragment, bundle)
        }
        binding.rvWatchlist.layoutManager = LinearLayoutManager(context)
        binding.rvWatchlist.adapter = movieAdapter

        // Setup Folders Adapter
        folderAdapter = FolderAdapter()
        binding.rvFolders.layoutManager = LinearLayoutManager(context)
        binding.rvFolders.adapter = folderAdapter
    }

    private fun observeViewModel() {
        // Observe Movies
        viewModel.watchlist.observe(viewLifecycleOwner) { list ->
            if (list.isNullOrEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.rvWatchlist.visibility = View.GONE
            } else {
                binding.emptyState.visibility = View.GONE
                binding.rvWatchlist.visibility = View.VISIBLE
                val movies = list.map { it.toMovie() }
                movieAdapter.submitList(movies)
            }
        }

        // Observe Folders
        viewModel.folders.observe(viewLifecycleOwner) { folders ->
            folderAdapter.submitList(folders)
        }
    }

    private fun setupListeners() {
        binding.btnExplore.setOnClickListener {
            val bundle = Bundle().apply { putBoolean("scroll_to_top", true) }
            findNavController().navigate(R.id.homeFragment, bundle)
        }

        binding.btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), CreateFolderActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
