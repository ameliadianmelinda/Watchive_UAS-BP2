package com.example.watchive.ui.watchlist

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.watchive.R
import com.example.watchive.data.local.WatchlistFolder
import com.example.watchive.databinding.ItemFolderBinding

class FolderAdapter(
    private val onItemClick: (WatchlistFolder) -> Unit
) : ListAdapter<WatchlistFolder, FolderAdapter.FolderViewHolder>(DIFF) {

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
            onItemClick(folder)
        }
    }

    class FolderViewHolder(private val binding: ItemFolderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: WatchlistFolder) {
            val context = itemView.context
            binding.tvFolderTitle.text = folder.title
            
            val sharedPrefUser = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userName = sharedPrefUser.getString("user_name", "User") ?: "User"
            binding.tvFolderUserInfo.text = "watchlist • $userName"

            val themePref = context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
            val isDarkMode = themePref.getBoolean("isDarkMode", true)
            
            if (isDarkMode) {
                binding.root.setCardBackgroundColor(Color.parseColor("#1E1E1E"))
                binding.cardFolderIcon.setCardBackgroundColor(Color.parseColor("#2A2A2A"))
                binding.ivFolderIcon.imageTintList = ColorStateList.valueOf(Color.WHITE)
                binding.tvFolderTitle.setTextColor(Color.WHITE)
                binding.tvFolderUserInfo.setTextColor(Color.parseColor("#B9B2BD"))
                binding.btnFolderMenu.imageTintList = ColorStateList.valueOf(Color.WHITE)
            } else {
                val brandColor = ContextCompat.getColor(context, R.color.brand)
                val darkPurple = ContextCompat.getColor(context, R.color.purple_dark)
                val whiteTrans = Color.parseColor("#80FFFFFF")
                
                binding.root.setCardBackgroundColor(whiteTrans)
                binding.cardFolderIcon.setCardBackgroundColor(darkPurple)
                binding.ivFolderIcon.imageTintList = ColorStateList.valueOf(brandColor)
                binding.tvFolderTitle.setTextColor(darkPurple)
                binding.tvFolderUserInfo.setTextColor(darkPurple)
                binding.btnFolderMenu.imageTintList = ColorStateList.valueOf(darkPurple)
            }
        }
    }
}
