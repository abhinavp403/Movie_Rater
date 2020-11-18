package com.dev.abhinav.movierater.model

import com.google.gson.annotations.SerializedName

class Trailer (
        @field:SerializedName("key") private var key: String,
        @field:SerializedName("name") private var name: String
) {
    constructor() : this("", "")

    fun setKey(key: String) {
        this.key = key
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getKey(): String {
        return key
    }

    fun getName(): String {
        return name
    }

}