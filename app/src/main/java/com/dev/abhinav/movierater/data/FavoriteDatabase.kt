package com.dev.abhinav.movierater.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dev.abhinav.movierater.dao.FavoriteDAO

@Database(entities = [FavoriteList::class], version = 1)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDAO
    companion object {
        private var favDatabase: FavoriteDatabase? = null

        fun getInstance(context: Context): FavoriteDatabase {
            if (favDatabase == null) {
                favDatabase = Room.databaseBuilder(context.applicationContext, FavoriteDatabase::class.java, "favorite.db")
                                .allowMainThreadQueries()
                                .fallbackToDestructiveMigration()
                                .build()
            }
            return favDatabase!!
        }
    }
}