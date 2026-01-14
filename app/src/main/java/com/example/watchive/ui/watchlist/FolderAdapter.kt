package com.example.watchive.ui.watchlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.watchive.R
import com.example.watchive.data.local.WatchlistFolder
import com.example.watchive.databinding.ItemFolderBinding

class FolderAdapter : ListAdapter<WatchlistFolder, FolderAdapter.FolderViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<WatchlistFolder>() {
            override fun areItemsTheSame(oldItem: WatchlistFolder, newItem: WatchlistFolder): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: WatchlistFolder, newItem: WatchlistFolder): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = ItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = getItem(position)
        holder.bind(folder)
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply { putInt("folderId", folder.id) }
            it.findNavController().navigate(R.id.folderDetailFragment, bundle)
        }
    }

    class FolderViewHolder(private val binding: ItemFolderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: WatchlistFolder) {
            binding.tvFolderTitle.text = folder.title
            
            // Perbaikan: Gunakan nilai default yang aman jika SharedPreferences kosong
            val sharedPref = itemView.context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userName = sharedPref.getString("user_name", "User") ?: "User"
            binding.tvFolderUserInfo.text = "watchlist • $userName"
        }
    }
}
