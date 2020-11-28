package com.dev.abhinav.movierater.model

import com.google.gson.annotations.SerializedName

class MovieDetailObject (
    @field:SerializedName("id") private var id: Int,
    @field:SerializedName("name") private var name: String,
    @field:SerializedName("english_name") private var language: String
) {
    constructor() : this(0, "", "")

    fun getId(): Int {
        return id
    }

    fun getName(): String {
        return name
    }

    fun getLanguage(): String {
        return language
    }
}