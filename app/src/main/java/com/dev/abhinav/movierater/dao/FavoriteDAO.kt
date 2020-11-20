package com.dev.abhinav.movierater.dao

import androidx.room.*
import com.dev.abhinav.movierater.data.FavoriteList

@Dao
interface FavoriteDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favoriteList: FavoriteList)

    @Query(value = "Select * from favorite")
    fun isFavorite() : Boolean

    @Query(value = "Select * from favorite")
    fun getAll() : List<FavoriteList>

    @Delete
    fun delete(favoriteList: FavoriteList)

    @Query("DELETE FROM favorite")
    fun deleteAll()
}