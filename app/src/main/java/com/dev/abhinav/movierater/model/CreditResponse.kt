package com.dev.abhinav.movierater.model

import com.google.gson.annotations.SerializedName

class CreditResponse (
    @field:SerializedName("id") private var id_cast: Int,
    @field:SerializedName("cast") private var cast: List<Cast>,
    @field:SerializedName("crew") private var crew: List<Crew>
) {
    constructor() : this(0, ArrayList<Cast>(), ArrayList<Crew>())

    fun getCast(): List<Cast> {
        return cast
    }

    fun getCrew(): List<Crew> {
        return crew
    }

}