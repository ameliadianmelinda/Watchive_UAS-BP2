package com.example.watchive.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.watchive.data.remote.model.Movie

@Entity(tableName = "watchlist")
data class WatchlistMovie(
    @PrimaryKey
    val id: Int,
    val userId: Int, // Menghubungkan ke User
    val title: String,
    val overview: String?,
    val posterPath: String?,
    val voteAverage: Double,
    val releaseDate: String?
) {
    fun toMovie(): Movie {
        return Movie(
            id = id,
            title = title,
            overview = overview ?: "",
            posterPath = posterPath,
            voteAverage = voteAverage,
            releaseDate = releaseDate,
            genreIds = null,
            genres = null,
            credits = null
        )
    }

    companion object {
        fun fromMovie(movie: Movie, userId: Int): WatchlistMovie {
            return WatchlistMovie(
                id = movie.id,
                userId = userId,
                title = movie.title,
                overview = movie.overview,
                posterPath = movie.posterPath,
                voteAverage = movie.voteAverage,
                releaseDate = movie.releaseDate
            )
        }
    }
}
