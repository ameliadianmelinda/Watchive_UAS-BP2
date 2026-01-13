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

class MovieAdapter(private val listener: (Movie) -> Unit) :
    ListAdapter<Movie, MovieAdapter.MovieViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
        holder.itemView.setOnClickListener { listener(movie) }
    }

    fun updateData(newMovies: List<Movie>) {
        submitList(newMovies)
    }

    class MovieViewHolder(private val binding: ItemMovieHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.title
            binding.movieRating.rating = (movie.voteAverage / 2).toFloat()

            val year = movie.releaseDate?.split("-")?.get(0) ?: "N/A"
            binding.movieYear.text = binding.root.context.getString(R.string.year_format, year)

            val genreName = when (movie.genreIds?.firstOrNull()) {
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
            binding.movieGenre.text = genreName

            val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
            binding.moviePoster.load(imageUrl) {
                crossfade(true)
                placeholder(R.drawable.login_bg_gradient)
                error(R.drawable.login_bg_gradient)
                transformations(RoundedCornersTransformation(12f))
            }
        }
    }
}
