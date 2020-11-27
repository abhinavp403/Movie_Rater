package com.dev.abhinav.movierater.model

import com.google.gson.annotations.SerializedName

class MovieDetailResponse (
        @field:SerializedName("poster_path") private var posterPath: String,
        @field:SerializedName("tagline") private var tagline: String,
        @field:SerializedName("overview") private var overview: String,
        @field:SerializedName("release_date") private var releaseDate: String,
        @field:SerializedName("genres") private var movieDetailObjects: List<MovieDetailObject>,
        @field:SerializedName("id") private var id: Int,
        @field:SerializedName("original_language") private var originalLanguage: String,
        @field:SerializedName("title") private var title: String,
        @field:SerializedName("backdrop_path") private var backdropPath: String,
        @field:SerializedName("popularity") private var popularity: Double,
        @field:SerializedName("vote_count") private var voteCount: Int,
        @field:SerializedName("video") private var video: Boolean,
        @field:SerializedName("vote_average") private var voteAverage: Double,
        @field:SerializedName("production_companies") private var studios: List<MovieDetailObject>,
        @field:SerializedName("spoken_languages") private var spokenLanguage: List<MovieDetailObject>,
        @field:SerializedName("runtime") private var runtime: Int,
) {
    constructor() : this("", "", "", "", ArrayList<MovieDetailObject>(), 0, "", "", "", 0.0, 0, false, 0.0, ArrayList<MovieDetailObject>(), ArrayList<MovieDetailObject>(), 0)
    private var baseImageUrl : String = "http://image.tmdb.org/t/p/w500"

    fun setPosterPath(posterPath: String) {
        this.posterPath = posterPath
    }

    fun setTagline(tagline: String) {
        this.tagline = tagline
    }

    fun setOverview(overview: String) {
        this.overview = overview
    }

    fun setReleaseDate(releaseDate: String) {
        this.releaseDate = releaseDate
    }

    fun setGenres(movieDetailObjects: List<MovieDetailObject>) {
        this.movieDetailObjects = movieDetailObjects
    }

    fun setId(id: Int) {
        this.id = id
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

    fun setStudios(studios: List<MovieDetailObject>) {
        this.studios = studios
    }

    fun setSpokenLanguages(spokenLanguage: List<MovieDetailObject>) {
        this.spokenLanguage = spokenLanguage
    }

    fun setRuntime(runtime: Int) {
        this.runtime = runtime
    }

    fun getPosterPath(): String {
        return "http://image.tmdb.org/t/p/w500$posterPath"
    }

    fun getTagline(): String {
        return tagline
    }

    fun getOverview(): String {
        return overview
    }

    fun getReleaseDate(): String {
        return releaseDate
    }

    fun getGenres(): List<MovieDetailObject> {
        return movieDetailObjects
    }

    fun getId(): Int {
        return id
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

    fun getStudios(): List<MovieDetailObject> {
        return studios
    }

    fun getSpokenLanguages(): List<MovieDetailObject> {
        return spokenLanguage
    }

    fun getRuntime(): Int {
        return runtime
    }

}