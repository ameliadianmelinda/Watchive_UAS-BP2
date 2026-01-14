package com.example.watchive

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
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
        applyTheme()
    }

    private fun applyTheme() {
        val sharedPref = requireActivity().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("isDarkMode", true)
        
        val context = requireContext()
        val brandColor = ContextCompat.getColor(context, R.color.brand)
        val darkPurple = ContextCompat.getColor(context, R.color.purple_dark)
        val whiteTrans = Color.parseColor("#80FFFFFF")

        if (isDarkMode) {
            binding.root.setBackgroundColor(Color.BLACK)
            binding.searchContainer.backgroundTintList = null
            binding.etSearchWatchlist.setTextColor(Color.WHITE)
            binding.etSearchWatchlist.setHintTextColor(Color.parseColor("#8E8E93"))
            binding.ivSearchIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#8E8E93"))
            binding.tvWatchlistTitle.setTextColor(Color.WHITE)
            binding.btnAdd.imageTintList = ColorStateList.valueOf(Color.WHITE)
            updateAllTextViewsColor(binding.root as ViewGroup, Color.WHITE)
        } else {
            binding.root.setBackgroundColor(brandColor)
            binding.searchContainer.backgroundTintList = ColorStateList.valueOf(whiteTrans)
            binding.etSearchWatchlist.setTextColor(darkPurple)
            binding.etSearchWatchlist.setHintTextColor(darkPurple)
            binding.ivSearchIcon.imageTintList = ColorStateList.valueOf(darkPurple)
            binding.tvWatchlistTitle.setTextColor(darkPurple)
            binding.btnAdd.imageTintList = ColorStateList.valueOf(darkPurple)
            updateAllTextViewsColor(binding.root as ViewGroup, darkPurple)
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
        // PERBAIKAN: Berikan perintah navigasi saat folder diklik
        folderAdapter = FolderAdapter { folder ->
            val bundle = Bundle().apply { putInt("folderId", folder.id) }
            findNavController().navigate(R.id.folderDetailFragment, bundle)
        }
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
