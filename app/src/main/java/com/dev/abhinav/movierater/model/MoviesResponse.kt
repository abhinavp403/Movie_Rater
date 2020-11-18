package com.dev.abhinav.movierater.model

import com.google.gson.annotations.SerializedName

class MoviesResponse (
    @field:SerializedName("page") private var page: Int,
    @field:SerializedName("results") private var results: List<Movie>,
    @field:SerializedName("total_results") private var totalResults: Int,
    @field:SerializedName("total_pages") private var totalPages: Int,
) {
    constructor() : this(0, ArrayList<Movie>(), 0, 0)

    fun setPage(page: Int) {
        this.page = page
    }

    fun setResults(results: List<Movie>) {
        this.results = results
    }

    fun setTotalResults(totalResults: Int) {
        this.totalResults = totalResults
    }

    fun setTotalPages(totalPages: Int) {
        this.totalPages = totalPages
    }

    fun getPage(): Int {
        return page
    }

    fun getResults(): List<Movie> {
        return results
    }

    fun getTotalResults(): Int {
        return totalResults
    }

    fun getTotalPages(): Int {
        return totalPages
    }

}