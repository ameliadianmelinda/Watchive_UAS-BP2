package com.example.watchive

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchive.databinding.FragmentWatchlistBinding
import com.example.watchive.ui.watchlist.CreateFolderActivity
import com.example.watchive.ui.watchlist.FolderAdapter
import com.example.watchive.ui.watchlist.WatchlistViewModel

class WatchlistFragment : Fragment() {

    private var _binding: FragmentWatchlistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WatchlistViewModel by viewModels()
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

        setupRecyclerView()
        observeViewModel()
        setupListeners()
        setupSearch()
    }

    private fun setupRecyclerView() {
        folderAdapter = FolderAdapter()
        binding.rvFolders.layoutManager = LinearLayoutManager(context)
        binding.rvFolders.adapter = folderAdapter
    }

    private fun setupSearch() {
        binding.etSearchWatchlist.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterFolders(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterFolders(query: String) {
        val allFolders = viewModel.folders.value ?: emptyList()
        val filteredFolders = if (query.isEmpty()) {
            allFolders
        } else {
            allFolders.filter { it.title.contains(query, ignoreCase = true) }
        }
        updateUI(filteredFolders)
    }

    private fun observeViewModel() {
        viewModel.folders.observe(viewLifecycleOwner) { folders ->
            filterFolders(binding.etSearchWatchlist.text.toString())
        }
    }

    private fun updateUI(folders: List<com.example.watchive.data.local.WatchlistFolder>) {
        val isFoldersEmpty = folders.isEmpty()

        if (isFoldersEmpty) {
            val originalEmpty = viewModel.folders.value.isNullOrEmpty()
            if (originalEmpty) {
                binding.emptyState.visibility = View.VISIBLE
                binding.layoutContent.visibility = View.GONE
            } else {
                // Jika pencarian tidak ada hasil
                binding.emptyState.visibility = View.GONE
                binding.layoutContent.visibility = View.VISIBLE
                folderAdapter.submitList(emptyList())
            }
        } else {
            binding.emptyState.visibility = View.GONE
            binding.layoutContent.visibility = View.VISIBLE
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
