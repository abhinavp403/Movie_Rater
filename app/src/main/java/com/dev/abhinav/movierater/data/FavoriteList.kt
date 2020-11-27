package com.dev.abhinav.movierater.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
class FavoriteList {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "movieid")
    var movieid: Int? = null

    @ColumnInfo(name = "title")
    var mtitle: String? = null

    @ColumnInfo(name = "runtime")
    var runtime: Int? = null

    @ColumnInfo(name = "posterpath")
    var posterpath: String? = null

    @ColumnInfo(name = "overview")
    var overview: String? = null

    @ColumnInfo(name = "releasedate")
    var releasedate: String? = null
}
