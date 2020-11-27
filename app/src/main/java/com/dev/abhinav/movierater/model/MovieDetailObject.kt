package com.dev.abhinav.movierater.model

import com.google.gson.annotations.SerializedName

class MovieDetailObject (
    @field:SerializedName("id") private var id: Int,
    @field:SerializedName("name") private var name: String,
) {
    constructor() : this(0, "")

    fun setId(id: Int) {
        this.id = id
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getId(): Int {
        return id
    }

    fun getName(): String {
        return name
    }
}