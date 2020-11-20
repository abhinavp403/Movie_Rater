package com.dev.abhinav.movierater

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.abhinav.movierater.adapter.TrailerAdapter
import com.dev.abhinav.movierater.api.Client
import com.dev.abhinav.movierater.api.Service
import com.dev.abhinav.movierater.data.FavoriteDatabase
import com.dev.abhinav.movierater.data.FavoriteList
import com.dev.abhinav.movierater.model.Trailer
import com.dev.abhinav.movierater.model.TrailerResponse
import com.github.ivbaranov.mfb.MaterialFavoriteButton
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailActivity : AppCompatActivity() {
    private lateinit var nameOfMovie: TextView
    private lateinit var plotSynopsis: TextView
    private lateinit var userRating: TextView
    private lateinit var releaseDate: TextView
    private lateinit var imageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrailerAdapter
    private lateinit var trailerList: List<Trailer>
    private lateinit var favoriteDatabase: FavoriteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        favoriteDatabase = FavoriteDatabase.getInstance(applicationContext)

        initCollapsingToolbar()

        imageView = findViewById(R.id.thumbnail_image_header)
        nameOfMovie = findViewById(R.id.title)
        plotSynopsis = findViewById(R.id.plotsynopsis)
        userRating = findViewById(R.id.userrating)
        releaseDate = findViewById(R.id.releasedate)

        val intent = intent
        if (intent.hasExtra("original_title")) {
            val thumbnail = intent.extras!!.getString("poster_path")
            val movieName = intent.extras!!.getString("original_title")
            val synopsis = intent.extras!!.getString("overview")
            val rating = intent.extras!!.getString("vote_average")
            val dateOfRelease = intent.extras!!.getString("release_date")

            Glide.with(this).load(thumbnail).placeholder(R.drawable.loading).into(imageView)
            nameOfMovie.text = movieName
            plotSynopsis.text = synopsis
            userRating.text = rating
            releaseDate.text = dateOfRelease
        } else {
            Toast.makeText(this@DetailActivity, "No API Data", Toast.LENGTH_SHORT).show()
        }

        val materialFavoriteButton: MaterialFavoriteButton = findViewById(R.id.fav_button)


        val sharedPrefs = getSharedPreferences("FavOrNot", MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPrefs.edit()
//        Log.d("ppp", sharedPrefs.getBoolean("fav", true).toString())
//        if (sharedPrefs.getBoolean("fav", true)) {
//            materialFavoriteButton.isFavorite = true
//        }

        materialFavoriteButton.setOnFavoriteChangeListener { buttonView, favorite ->
            if(favorite) {
                editor.putBoolean("fav", true)
                editor.apply()
                saveFavorite()
                Snackbar.make(buttonView, "Added to Favorites", Snackbar.LENGTH_SHORT).show()
            } else {
                removeFavorite()
                editor.putBoolean("fav", false)
                editor.apply()
                Snackbar.make(buttonView, "Removed from Favorites", Snackbar.LENGTH_SHORT).show()
            }
        }
        Log.d("ppp", editor.toString())
        initViews()
    }

    private fun initCollapsingToolbar() {
        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        collapsingToolbar.title = ""
        val appBarLayout: AppBarLayout = findViewById(R.id.appbar)
        appBarLayout.setExpanded(true)

        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.title = intent.extras!!.getString("original_title")
                    isShow = true
                } else if (isShow) {
                    collapsingToolbar.title = ""
                    isShow = false
                }
            }
        })
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recycler_view1)
        trailerList = ArrayList()
        adapter = TrailerAdapter(this, trailerList)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        loadJSON()
    }

    private fun loadJSON() {
        val movieId = intent.extras!!.getInt("id")
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this@DetailActivity, "Please obtain API Key first", Toast.LENGTH_SHORT).show()
                return
            }
            val client = Client()
            val service = client.getClient().create(Service::class.java)
            val call = service.getMovieTrailer(movieId, BuildConfig.THE_MOVIE_DB_API_TOKEN)
            call!!.enqueue(object : Callback<TrailerResponse?> {
                override fun onResponse(call: Call<TrailerResponse?>, response: Response<TrailerResponse?>) {
                    val trailers: List<Trailer> = response.body()!!.getResults()
                    recyclerView.adapter = TrailerAdapter(applicationContext, trailers)
                    recyclerView.smoothScrollToPosition(0)
                }

                override fun onFailure(call: Call<TrailerResponse?>, t: Throwable) {
                    Log.d("Error", t.message)
                    Toast.makeText(this@DetailActivity, "Error fetching trailer data", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Log.d("Error", e.message)
            Toast.makeText(this@DetailActivity, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveFavorite() {
        val favoriteList = FavoriteList()
        favoriteList.movieid = intent.extras!!.getInt("id")
        favoriteList.mtitle = nameOfMovie.text.toString().trim()
        favoriteList.posterpath = intent.extras!!.getString("poster_path")
        favoriteList.userrating = intent.extras!!.getString("vote_average")!!.toDouble()
        favoriteList.overview = plotSynopsis.text.toString().trim()
        favoriteList.releasedate = releaseDate.text.toString().trim()
        favoriteDatabase.favoriteDao().insert(favoriteList)
    }

    private fun removeFavorite() {
        val favoriteList = FavoriteList()
        favoriteList.movieid = intent.extras!!.getInt("id")
        favoriteList.mtitle = nameOfMovie.text.toString().trim()
        favoriteList.posterpath = intent.extras!!.getString("poster_path")
        favoriteList.userrating = intent.extras!!.getString("vote_average")!!.toDouble()
        favoriteList.overview = plotSynopsis.text.toString().trim()
        favoriteList.releasedate = releaseDate.text.toString().trim()
        favoriteDatabase.favoriteDao().delete(favoriteList)
    }
}