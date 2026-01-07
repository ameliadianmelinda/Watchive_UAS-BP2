package com.example.watchive.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.watchive.R
import com.example.watchive.data.remote.model.Movie
import com.example.watchive.databinding.ItemMovieHorizontalBinding

class MovieAdapter(private var movies: List<Movie>, private val listener: (Movie) -> Unit) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
        holder.itemView.setOnClickListener { listener(movie) }
    }

    override fun getItemCount(): Int = movies.size

    fun updateData(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    class MovieViewHolder(private val binding: ItemMovieHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.title
            binding.movieRating.rating = (movie.voteAverage / 2).toFloat() // Konversi skala 10 ke 5 bintang
            
            // Mengambil tahun dari releaseDate (format: YYYY-MM-DD)
            val year = movie.releaseDate?.split("-")?.get(0) ?: "N/A"
            binding.movieYear.text = "($year)"

            // Pemetaan Genre Sederhana (TMDB Genre IDs)
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
            }
        }
    }
}
