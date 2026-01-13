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
import androidx.recyclerview.widget.LinearLayoutManager
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
            onLongClick = { movie ->
                showRemoveMovieFromFolderDialog(movie)
            },
            listener = { movie ->
                val bundle = Bundle().apply { putInt("movieId", movie.id) }
                findNavController().navigate(resId = R.id.movieDetailFragment, args = bundle)
            }
        )
        binding.rvFolderMovies.layoutManager = LinearLayoutManager(context)
        binding.rvFolderMovies.adapter = adapter
    }

    private fun showRemoveMovieFromFolderDialog(movie: Movie) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus dari Folder")
            .setMessage("Hapus '${movie.title}' dari folder ini?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.removeMovieFromFolder(folderId, movie.id)
                Toast.makeText(context, "${movie.title} dihapus dari folder", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.folder.observe(viewLifecycleOwner) { folder ->
            folder?.let {
                binding.tvFolderTitle.text = it.title
                binding.tvFolderDesc.text = it.description ?: "Tidak ada deskripsi"
            }
        }

        viewModel.getMoviesInFolder(folderId).observe(viewLifecycleOwner) { movies ->
            adapter.submitList(movies.map { it.toMovie() })
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
            showAddMovieToFolderDialog()
        }
    }

    private fun showAddMovieToFolderDialog() {
        val db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            val allWatchlistMovies = withContext(Dispatchers.IO) { db.watchlistDao().getAllStatic() }
            val moviesInFolder = viewModel.getMoviesInFolder(folderId).value ?: emptyList()
            val moviesInFolderIds = moviesInFolder.map { it.id }.toSet()
            
            val availableMovies = allWatchlistMovies.filter { it.id !in moviesInFolderIds }

            if (availableMovies.isEmpty()) {
                Toast.makeText(context, "Semua film di watchlist sudah ada di folder ini", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val movieTitles = availableMovies.map { it.title }.toTypedArray()
            AlertDialog.Builder(requireContext())
                .setTitle("Tambah Film ke Folder")
                .setItems(movieTitles) { _, which ->
                    val selectedMovie = availableMovies[which]
                    viewModel.addMovieToFolder(folderId, selectedMovie.id)
                    Toast.makeText(context, "${selectedMovie.title} ditambahkan", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
    }

    private fun showMenuDialog() {
        val options = arrayOf("Edit Nama Folder", "Hapus Folder")
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
            .setTitle("Edit Folder")
            .setView(view)
            .setPositiveButton("Simpan") { _, _ ->
                val newTitle = etTitle.text.toString()
                val newDesc = etDesc.text.toString()
                viewModel.updateFolder(folderId, newTitle, newDesc)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showDeleteConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Folder")
            .setMessage("Apakah Anda yakin ingin menghapus folder ini?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.folder.value?.let {
                    viewModel.deleteFolder(it)
                    findNavController().navigateUp()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
