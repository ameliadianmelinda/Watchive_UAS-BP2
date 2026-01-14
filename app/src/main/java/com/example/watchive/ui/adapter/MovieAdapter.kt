package com.example.watchive.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.watchive.R
import com.example.watchive.data.remote.model.Movie
import com.example.watchive.databinding.ItemMovieHorizontalBinding
import com.example.watchive.databinding.ItemMovieGridBinding

class MovieAdapter(
    private val useGridLayout: Boolean = false,
    private val onLongClick: ((Movie) -> Unit)? = null,
    private val listener: (Movie) -> Unit
) : ListAdapter<Movie, RecyclerView.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (useGridLayout) {
            val binding = ItemMovieGridBinding.inflate(inflater, parent, false)
            MovieGridViewHolder(binding)
        } else {
            val binding = ItemMovieHorizontalBinding.inflate(inflater, parent, false)
            MovieHorizontalViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie = getItem(position)
        if (holder is MovieHorizontalViewHolder) {
            holder.bind(movie)
        } else if (holder is MovieGridViewHolder) {
            holder.bind(movie)
        }
        
        holder.itemView.setOnClickListener { listener(movie) }
        holder.itemView.setOnLongClickListener {
            onLongClick?.invoke(movie)
            true
        }
    }

    class MovieHorizontalViewHolder(private val binding: ItemMovieHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.title
            binding.movieRating.rating = (movie.voteAverage / 2).toFloat()
            val year = movie.releaseDate?.split("-")?.get(0) ?: "N/A"
            binding.movieYear.text = binding.root.context.getString(R.string.year_format, year)
            binding.movieGenre.text = getGenreName(movie.genreIds?.firstOrNull())
            binding.moviePoster.load("https://image.tmdb.org/t/p/w500${movie.posterPath}") {
                crossfade(true)
                placeholder(R.drawable.login_bg_gradient)
                transformations(RoundedCornersTransformation(12f))
            }
        }
    }

    class MovieGridViewHolder(private val binding: ItemMovieGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.title
            binding.movieYear.text = movie.releaseDate?.split("-")?.get(0) ?: "N/A"
            binding.moviePoster.load("https://image.tmdb.org/t/p/w500${movie.posterPath}") {
                crossfade(true)
                placeholder(R.drawable.login_bg_gradient)
                transformations(RoundedCornersTransformation(12f))
            }
        }
    }
}

private fun getGenreName(id: Int?): String {
    return when (id) {
        28 -> "Action"
        12 -> "Adventure"
        16 -> "Animation"
        35 -> "Comedy"
        80 -> "Crime"
        18 -> "Drama"
        10751 -> "Family"
        14 -> "Fantasy"
        27 -> "Horror"
        10749 -> "Romance"
        878 -> "Sci-Fi"
        53 -> "Thriller"
        else -> "Genre"
    }
}
