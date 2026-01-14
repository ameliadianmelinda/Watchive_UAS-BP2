package com.example.watchive.ui.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.watchive.R
import com.example.watchive.data.local.AppDatabase
import com.example.watchive.data.local.FolderMovieJoin
import com.example.watchive.data.remote.model.Movie
import com.example.watchive.databinding.FragmentFolderDetailBinding
import com.example.watchive.ui.adapter.MovieAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FolderDetailFragment : Fragment() {

    private var _binding: FragmentFolderDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FolderDetailViewModel by viewModels()
    private var folderId: Int = -1
    private lateinit var adapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFolderDetailBinding.inflate(inflater, container, false)
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
        observeViewModel()
        setupListeners()

        viewModel.loadFolder(folderId)
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(
            useGridLayout = true,
            onLongClick = { movie ->
                showRemoveMovieFromFolderDialog(movie)
            },
            listener = { movie ->
                val bundle = Bundle().apply { putInt("movieId", movie.id) }
                findNavController().navigate(resId = R.id.movieDetailFragment, args = bundle)
            }
        )
        binding.rvFolderMovies.layoutManager = GridLayoutManager(context, 2)
        binding.rvFolderMovies.adapter = adapter
    }

    private fun showRemoveMovieFromFolderDialog(movie: Movie) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.folder_remove_movie_title))
            .setMessage(getString(R.string.folder_remove_movie_message, movie.title))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.removeMovieFromFolder(folderId, movie.id)
                Toast.makeText(context, getString(R.string.folder_remove_movie_success, movie.title), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.folder.observe(viewLifecycleOwner) { folder ->
            folder?.let {
                binding.tvFolderTitle.text = it.title
                binding.tvFolderDesc.text = it.description ?: getString(R.string.no_description)
            }
        }

        viewModel.getMoviesInFolder(folderId).observe(viewLifecycleOwner) { movies ->
            if (movies.isNullOrEmpty()) {
                binding.rvFolderMovies.visibility = View.GONE
                binding.tvEmptyFolder.visibility = View.VISIBLE
            } else {
                binding.rvFolderMovies.visibility = View.VISIBLE
                binding.tvEmptyFolder.visibility = View.GONE
                adapter.submitList(movies.map { it.toMovie() })
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnMore.setOnClickListener {
            showMenuDialog()
        }

        binding.btnAddMovieToFolder.setOnClickListener {
            val bundle = Bundle().apply { putInt("folderId", folderId) }
            findNavController().navigate(resId = R.id.selectMoviesFragment, args = bundle)
        }
    }

    private fun showMenuDialog() {
        val options = arrayOf(getString(R.string.menu_edit_folder), getString(R.string.menu_delete_folder))
        AlertDialog.Builder(requireContext())
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditDialog()
                    1 -> showDeleteConfirmDialog()
                }
            }
            .show()
    }

    private fun showEditDialog() {
        val currentFolder = viewModel.folder.value ?: return
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_folder, null)
        val etTitle = view.findViewById<EditText>(R.id.et_edit_title)
        val etDesc = view.findViewById<EditText>(R.id.et_edit_desc)

        etTitle.setText(currentFolder.title)
        etDesc.setText(currentFolder.description)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_edit_folder_title))
            .setView(view)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val newTitle = etTitle.text.toString()
                val newDesc = etDesc.text.toString()
                viewModel.updateFolder(folderId, newTitle, newDesc)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showDeleteConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_delete_folder_title))
            .setMessage(getString(R.string.dialog_delete_folder_message))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.folder.value?.let {
                    viewModel.deleteFolder(it)
                    findNavController().navigateUp()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
