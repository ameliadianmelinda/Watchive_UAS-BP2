package com.example.watchive.data.remote

import com.example.watchive.data.remote.model.CreditsResponse
import com.example.watchive.data.remote.model.Movie
import com.example.watchive.data.remote.model.MoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = "8d3806955bdfd6164f6561b5fd6ab8b7",
        @Query("page") page: Int = 1
    ): Response<MoviesResponse>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String = "8d3806955bdfd6164f6561b5fd6ab8b7",
        @Query("page") page: Int = 1
    ): Response<MoviesResponse>

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String = "8d3806955bdfd6164f6561b5fd6ab8b7",
        @Query("page") page: Int = 1
    ): Response<MoviesResponse>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String = "8d3806955bdfd6164f6561b5fd6ab8b7",
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): Response<MoviesResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = "8d3806955bdfd6164f6561b5fd6ab8b7",
        @Query("append_to_response") appendToResponse: String = "credits"
    ): Response<Movie>
}
