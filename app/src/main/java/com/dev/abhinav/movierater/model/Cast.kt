package com.dev.abhinav.movierater.model

import com.google.gson.annotations.SerializedName

class Cast (
    @field:SerializedName("id") private var id: Int,
    @field:SerializedName("known_for_department") private var knownForDept: String,
    @field:SerializedName("name") private var name: String,
    @field:SerializedName("profile_path") private var profilePath: String,
    @field:SerializedName("character") private var character: String,
) {
    constructor() : this(0, "", "", "", "")

    fun setId(id: Int) {
        this.id = id
    }

    fun setKnownForDept(knownForDept: String) {
        this.knownForDept = knownForDept
    }

    fun setName(name: String) {
        this.name = name
    }

    fun setProfilePath(profilePath: String) {
        this.profilePath = profilePath
    }

    fun setCharacter(character: String) {
        this.character = character
    }

    fun getId(): Int {
        return id
    }

    fun getKnownForDept(): String {
        return knownForDept
    }

    fun getName(): String {
        return name
    }

    fun getProfilePath(): String {
        return "http://image.tmdb.org/t/p/w500$profilePath"
    }

    fun getCharacter(): String {
        return character
    }
}