package com.dev.abhinav.movierater.api

import com.dev.abhinav.movierater.model.CreditResponse
import com.dev.abhinav.movierater.model.MoviesResponse
import com.dev.abhinav.movierater.model.TrailerResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Service {

    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String?): Call<MoviesResponse?>?

    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("api_key") apiKey: String?): Call<MoviesResponse?>?

    @GET("movie/{movie_id}/videos")
    fun getMovieTrailer(@Path("movie_id") id: Int?, @Query("api_key") apiKey: String?): Call<TrailerResponse?>?

    @GET("movie/{movie_id}/credits")
    fun getCredits(@Path("movie_id") id: Int?, @Query("api_key") apiKey: String?): Call<CreditResponse?>?
}