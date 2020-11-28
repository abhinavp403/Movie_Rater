package com.dev.abhinav.movierater.api

import com.dev.abhinav.movierater.model.CreditResponse
import com.dev.abhinav.movierater.model.MovieDetailResponse
import com.dev.abhinav.movierater.model.MoviesResponse
import com.dev.abhinav.movierater.model.TrailerResponse
import retrofit2.Call
import retrofit2.http.*

interface Service {

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") id: Int?, @Query("api_key") apiKey: String?): Call<MovieDetailResponse?>?

    @GET("movie/popular")
    fun getPopularMovies2(@QueryMap params: HashMap<String?, String?>): Call<MoviesResponse?>?

    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String?): Call<MoviesResponse?>?

    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("api_key") apiKey: String?): Call<MoviesResponse?>?

    @GET("movie/{movie_id}/videos")
    fun getMovieTrailer(@Path("movie_id") id: Int?, @Query("api_key") apiKey: String?): Call<TrailerResponse?>?

    @GET("movie/{movie_id}/credits")
    fun getCredits(@Path("movie_id") id: Int?, @Query("api_key") apiKey: String?): Call<CreditResponse?>?
}