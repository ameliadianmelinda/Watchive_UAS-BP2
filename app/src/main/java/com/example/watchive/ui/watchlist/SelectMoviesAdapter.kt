package com.example.watchive.ui.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.watchive.data.local.WatchlistMovie
import com.example.watchive.databinding.ItemMovieSelectableBinding

class SelectMoviesAdapter(
    private val movies: List<WatchlistMovie>,
    private val onSelectionChanged: (WatchlistMovie, Boolean) -> Unit
) : RecyclerView.Adapter<SelectMoviesAdapter.ViewHolder>() {

    private val selectedMovieIds = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieSelectableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.binding.movieTitle.text = movie.title
        holder.binding.movieYear.text = movie.releaseDate?.split("-")?.get(0) ?: ""
        holder.binding.moviePoster.load("https://image.tmdb.org/t/p/w200${movie.posterPath}")
        
        holder.binding.checkboxSelect.isChecked = selectedMovieIds.contains(movie.id)

        holder.itemView.setOnClickListener {
            val isChecked = !holder.binding.checkboxSelect.isChecked
            holder.binding.checkboxSelect.isChecked = isChecked
            if (isChecked) selectedMovieIds.add(movie.id) else selectedMovieIds.remove(movie.id)
            onSelectionChanged(movie, isChecked)
        }
    }

    override fun getItemCount(): Int = movies.size

    class ViewHolder(val binding: ItemMovieSelectableBinding) : RecyclerView.ViewHolder(binding.root)
}
