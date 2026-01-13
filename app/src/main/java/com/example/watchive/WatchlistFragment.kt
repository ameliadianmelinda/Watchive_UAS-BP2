package com.example.watchive

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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
        // Setup Movies Adapter with Long Click for Deletion
        movieAdapter = MovieAdapter(
            onLongClick = { movie ->
                showDeleteMovieDialog(movie.id, movie.title)
            },
            listener = { movie ->
                val bundle = Bundle().apply { putInt("movieId", movie.id) }
                findNavController().navigate(resId = R.id.movieDetailFragment, args = bundle)
            }
        )
        binding.rvWatchlist.layoutManager = LinearLayoutManager(context)
        binding.rvWatchlist.adapter = movieAdapter

        // Setup Folders Adapter
        folderAdapter = FolderAdapter()
        binding.rvFolders.layoutManager = LinearLayoutManager(context)
        binding.rvFolders.adapter = folderAdapter
    }

    private fun showDeleteMovieDialog(movieId: Int, title: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Film")
            .setMessage("Apakah Anda yakin ingin menghapus '$title' dari watchlist?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.remove(movieId)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.watchlist.observe(viewLifecycleOwner) { movies ->
            val folders = viewModel.folders.value ?: emptyList()
            updateUI(movies ?: emptyList(), folders)
        }

        viewModel.folders.observe(viewLifecycleOwner) { folders ->
            val movies = viewModel.watchlist.value ?: emptyList()
            updateUI(movies, folders ?: emptyList())
        }
    }

    private fun updateUI(movies: List<com.example.watchive.data.local.WatchlistMovie>, folders: List<com.example.watchive.data.local.WatchlistFolder>) {
        val isMoviesEmpty = movies.isEmpty()
        val isFoldersEmpty = folders.isEmpty()

        binding.tvFolderHeader.visibility = if (isFoldersEmpty) View.GONE else View.VISIBLE
        binding.rvFolders.visibility = if (isFoldersEmpty) View.GONE else View.VISIBLE
        
        binding.tvMovieHeader.visibility = if (isMoviesEmpty) View.GONE else View.VISIBLE
        binding.rvWatchlist.visibility = if (isMoviesEmpty) View.GONE else View.VISIBLE

        if (isMoviesEmpty && isFoldersEmpty) {
            binding.emptyState.visibility = View.VISIBLE
            binding.layoutContent.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.layoutContent.visibility = View.VISIBLE
            
            movieAdapter.submitList(movies.map { it.toMovie() })
            folderAdapter.submitList(folders)
        }
    }

    private fun setupListeners() {
        binding.btnExplore.setOnClickListener {
            val bundle = Bundle().apply { putBoolean("scroll_to_top", true) }
            findNavController().navigate(resId = R.id.homeFragment, args = bundle)
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
