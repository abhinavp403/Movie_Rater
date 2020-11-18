package com.dev.abhinav.movierater.model

import com.google.gson.annotations.SerializedName

class Movie (
    @field:SerializedName("poster_path") private var posterPath: String,
    @field:SerializedName("adult") private var adult: Boolean,
    @field:SerializedName("overview") private var overview: String,
    @field:SerializedName("release_date") private var releaseDate: String,
    @field:SerializedName("genre_ids") private var genreIds: List<Int>,
    @field:SerializedName("id") private var id: Int,
    @field:SerializedName("original_title") private var originalTitle: String,
    @field:SerializedName("original_language") private var originalLanguage: String,
    @field:SerializedName("title") private var title: String,
    @field:SerializedName("backdrop_path") private var backdropPath: String,
    @field:SerializedName("popularity") private var popularity: Double,
    @field:SerializedName("vote_count") private var voteCount: Int,
    @field:SerializedName("video") private var video: Boolean,
    @field:SerializedName("vote_average") private var voteAverage: Double
) {
    constructor() : this("", false, "", "", ArrayList<Int>(), 0, "", "", "", "", 0.0, 0, false, 0.0)
    private var baseImageUrl : String = "http://image.tmdb.org/t/p/w500"

    fun setPosterPath(posterPath: String) {
        this.posterPath = posterPath
    }

    fun setAdult(adult: Boolean) {
        this.adult = adult
    }

    fun setOverview(overview: String) {
        this.overview = overview
    }

    fun setReleaseDate(releaseDate: String) {
        this.releaseDate = releaseDate
    }

    fun setGenreIds(genreIds: List<Int>) {
        this.genreIds = genreIds
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun setOriginalTitle(originalTitle: String) {
        this.originalTitle = originalTitle
    }

    fun setOriginalLanguage(originalLanguage: String) {
        this.originalLanguage = originalLanguage
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun setBackdropPath(backdropPath: String) {
        this.backdropPath = backdropPath
    }

    fun setPopularity(popularity: Double) {
        this.popularity = popularity
    }

    fun setVoteCount(voteCount: Int) {
        this.voteCount = voteCount
    }

    fun setVideo(video: Boolean) {
        this.video = video
    }

    fun setVoteAverage(voteAverage: Double) {
        this.voteAverage = voteAverage
    }
    
    fun getPosterPath(): String {
        return "http://image.tmdb.org/t/p/w500$posterPath"
    }

    fun getAdult(): Boolean {
        return adult
    }

    fun getOverview(): String {
        return overview
    }

    fun getReleaseDate(): String {
        return releaseDate
    }

    fun getGenreIds(): List<Int> {
        return genreIds
    }

    fun getId(): Int {
        return id
    }

    fun getOriginalTitle(): String {
        return originalTitle
    }

    fun getOriginalLanguage(): String {
        return originalLanguage
    }

    fun getTitle(): String {
        return title
    }

    fun getBackdropPath(): String {
        return backdropPath
    }

    fun getPopularity(): Double {
        return popularity
    }

    fun getVoteCount(): Int {
        return voteCount
    }

    fun getVideo(): Boolean {
        return video
    }

    fun getVoteAverage(): Double {
        return voteAverage
    }

}