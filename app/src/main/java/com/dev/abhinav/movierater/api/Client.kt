package com.dev.abhinav.movierater.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Client {
    private val BASE_URL : String = "http://api.themoviedb.org/3/"
    private var retrofit: Retrofit? = null

    fun getClient() : Retrofit {
        if(retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}