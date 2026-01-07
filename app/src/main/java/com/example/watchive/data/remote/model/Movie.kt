package com.example.watchive.data.remote.model

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("overview")
    val overview: String,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("vote_average")
    val voteAverage: Double,

    @SerializedName("release_date")
    val releaseDate: String?, // Kita tambahkan ini untuk mengambil tahun

    @SerializedName("genre_ids")
    val genreIds: List<Int>?,

    @SerializedName("genres")
    val genres: List<Genre>?
)
