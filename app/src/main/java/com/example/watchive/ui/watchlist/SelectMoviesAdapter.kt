package com.example.watchive.ui.watchlist

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.watchive.R
import com.example.watchive.data.remote.model.Movie
import com.example.watchive.databinding.ItemMovieSelectableBinding

class SelectMoviesAdapter(
    private val movies: List<Movie>,
    private val onSelectionChanged: (Movie, Boolean) -> Unit
) : RecyclerView.Adapter<SelectMoviesAdapter.ViewHolder>() {

    private val selectedMovieIds = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieSelectableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        val context = holder.itemView.context
        
        holder.binding.movieTitle.text = movie.title
        holder.binding.movieYear.text = movie.releaseDate?.split("-")?.get(0) ?: ""
        holder.binding.moviePoster.load("https://image.tmdb.org/t/p/w200${movie.posterPath}")
        
        holder.binding.checkboxSelect.isChecked = selectedMovieIds.contains(movie.id)

        // LOGIKA TEMA GLOBAL
        val themePref = context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val isDarkMode = themePref.getBoolean("isDarkMode", true)
        
        if (isDarkMode) {
            // DARK MODE
            holder.binding.movieTitle.setTextColor(Color.WHITE)
            holder.binding.movieYear.setTextColor(Color.parseColor("#B9B2BD"))
            holder.binding.checkboxSelect.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple_light))
        } else {
            // LIGHT MODE (Matahari Aktif)
            val darkPurple = ContextCompat.getColor(context, R.color.purple_dark)
            
            holder.binding.movieTitle.setTextColor(darkPurple)
            holder.binding.movieYear.setTextColor(darkPurple)
            // Sesuaikan warna checkbox agar tetap ungu tua yang serasi
            holder.binding.checkboxSelect.buttonTintList = ColorStateList.valueOf(darkPurple)
        }

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
