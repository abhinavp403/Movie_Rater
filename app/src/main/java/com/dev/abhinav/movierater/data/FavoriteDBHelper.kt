package com.dev.abhinav.movierater.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.dev.abhinav.movierater.model.Movie

class FavoriteDBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private lateinit var sqLiteOpenHelper: SQLiteOpenHelper
    private lateinit var db: SQLiteDatabase
    private lateinit var mContext: Context

    companion object {
        const val DATABASE_NAME = "favorite.db"
        const val DATABASE_VERSION = 1
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        this.db = sqLiteOpenHelper.writableDatabase
    }

    override fun close() {
        sqLiteOpenHelper.close()
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + FavoriteContract.FavoriteEntry.TABLE_NAME + " ( " +
                    FavoriteContract.FavoriteEntry.COLUMN_MOVIEID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FavoriteContract.FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                    FavoriteContract.FavoriteEntry.COLUMN_USERRATING + " REAL NOT NULL, " +
                    FavoriteContract.FavoriteEntry.COLUMN_POSTERPATH + " TEXT NOT NULL, " +
                    FavoriteContract.FavoriteEntry.COLUMN_PLOTSYNOPSIS + " TEXT NOT NULL, " +
                    ");"
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE)
        Log.d("cc", DATABASE_NAME)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + FavoriteContract.FavoriteEntry.TABLE_NAME)
        onCreate(sqLiteDatabase)
    }

    fun addFavorite(movie: Movie) {
        val db: SQLiteDatabase = this.writableDatabase
        Log.d("db", "here")
        val values = ContentValues()
        values.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIEID, movie.getId())
        values.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, movie.getOriginalTitle())
        values.put(FavoriteContract.FavoriteEntry.COLUMN_USERRATING, movie.getVoteAverage())
        values.put(FavoriteContract.FavoriteEntry.COLUMN_POSTERPATH, movie.getPosterPath())
        values.put(FavoriteContract.FavoriteEntry.COLUMN_PLOTSYNOPSIS, movie.getOverview())

        db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, values)
        db.close()
    }

    fun deleteFavorite(id: Int) {
        val db: SQLiteDatabase = this.writableDatabase
        db.delete(FavoriteContract.FavoriteEntry.TABLE_NAME, FavoriteContract.FavoriteEntry.COLUMN_MOVIEID + "=" + id, null)
    }

    fun getAllFavorite(): MutableList<Movie> {
        val columns = arrayOf(
                FavoriteContract.FavoriteEntry.COLUMN_MOVIEID,
                FavoriteContract.FavoriteEntry.COLUMN_TITLE,
                FavoriteContract.FavoriteEntry.COLUMN_USERRATING,
                FavoriteContract.FavoriteEntry.COLUMN_POSTERPATH,
                FavoriteContract.FavoriteEntry.COLUMN_PLOTSYNOPSIS
        )
        val sortOrder = FavoriteContract.FavoriteEntry.COLUMN_MOVIEID + " ASC"
        val favList: MutableList<Movie> = ArrayList()
        val db: SQLiteDatabase = this.readableDatabase
        val cursor = db.query(FavoriteContract.FavoriteEntry.TABLE_NAME, columns, null, null, null, null, sortOrder)
        if(cursor.moveToFirst()) {
            do {
                val movie = Movie()
                movie.setId(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_MOVIEID)).toInt())
                movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TITLE)))
                movie.setVoteAverage(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_USERRATING)).toDouble())
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_POSTERPATH)))
                movie.setOverview(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_PLOTSYNOPSIS)))
                favList.add(movie)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return favList
    }
}