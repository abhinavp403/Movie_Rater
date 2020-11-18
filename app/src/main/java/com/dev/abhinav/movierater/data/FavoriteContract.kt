package com.dev.abhinav.movierater.data

import android.provider.BaseColumns

class FavoriteContract {
    object FavoriteEntry : BaseColumns {
        const val TABLE_NAME = "favorite"
        const val COLUMN_MOVIEID = "movieid"
        const val COLUMN_TITLE = "title"
        const val COLUMN_USERRATING = "userrating"
        const val COLUMN_POSTERPATH = "posterpath"
        const val COLUMN_PLOTSYNOPSIS = "overview"
    }
}