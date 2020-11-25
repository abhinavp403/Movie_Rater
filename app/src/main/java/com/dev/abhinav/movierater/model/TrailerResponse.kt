package com.dev.abhinav.movierater.model

import com.google.gson.annotations.SerializedName

class TrailerResponse (
    @field:SerializedName("id") private var id_trailer: Int,
    @field:SerializedName("results") private var results: List<Trailer>
) {
    constructor() : this(0, ArrayList<Trailer>())

    fun getResults(): List<Trailer> {
        return results
    }

}