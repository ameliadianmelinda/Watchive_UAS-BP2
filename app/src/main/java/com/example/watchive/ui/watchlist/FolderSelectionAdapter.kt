package com.example.watchive.ui.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.watchive.data.local.WatchlistFolder
import com.example.watchive.databinding.ItemFolderSelectionBinding

class FolderSelectionAdapter(
    private val folders: List<WatchlistFolder>,
    private val onFolderSelected: (WatchlistFolder) -> Unit
) : RecyclerView.Adapter<FolderSelectionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFolderSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = folders[position]
        holder.binding.tvFolderName.text = folder.title
        holder.itemView.setOnClickListener { onFolderSelected(folder) }
    }

    override fun getItemCount(): Int = folders.size

    class ViewHolder(val binding: ItemFolderSelectionBinding) : RecyclerView.ViewHolder(binding.root)
}
