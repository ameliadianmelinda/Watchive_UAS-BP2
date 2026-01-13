package com.example.watchive.ui.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchive.R
import com.example.watchive.databinding.FragmentFolderDetailBinding
import com.example.watchive.ui.adapter.MovieAdapter

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
        adapter = MovieAdapter { movie ->
            val bundle = Bundle().apply { putInt("movieId", movie.id) }
            findNavController().navigate(R.id.movieDetailFragment, bundle)
        }
        binding.rvFolderMovies.layoutManager = LinearLayoutManager(context)
        binding.rvFolderMovies.adapter = adapter
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
