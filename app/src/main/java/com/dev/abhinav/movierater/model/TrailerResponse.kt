package com.dev.abhinav.movierater.model

import com.google.gson.annotations.SerializedName

class TrailerResponse (
    @field:SerializedName("id") private var id_trailer: Int,
    @field:SerializedName("results") private var results: List<Trailer>
) {
    constructor() : this(0, ArrayList<Trailer>())

    fun setIdTrailer(id_trailer: Int) {
        this.id_trailer = id_trailer
    }

    fun setResults(results: List<Trailer>) {
        this.results = results
    }

    fun getIdTrailer(): Int {
        return id_trailer
    }

    fun getResults(): List<Trailer> {
        return results
    }

}