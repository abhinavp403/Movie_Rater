package com.dev.abhinav.movierater

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.abhinav.movierater.adapter.TrailerAdapter
import com.dev.abhinav.movierater.api.Client
import com.dev.abhinav.movierater.api.Service
import com.dev.abhinav.movierater.data.FavoriteDBHelper
import com.dev.abhinav.movierater.model.Movie
import com.dev.abhinav.movierater.model.Trailer
import com.dev.abhinav.movierater.model.TrailerResponse
import com.github.ivbaranov.mfb.MaterialFavoriteButton
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class DetailActivity : AppCompatActivity() {
    private lateinit var nameOfMovie: TextView
    private lateinit var plotSynopsis: TextView
    private lateinit var userRating: TextView
    private lateinit var releaseDate: TextView
    private lateinit var imageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrailerAdapter
    private lateinit var trailerList: List<Trailer>
    private lateinit var favoriteDBHelper: FavoriteDBHelper
    private lateinit var movie: Movie
    private val activity: AppCompatActivity = this@DetailActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        materialFavoriteButton.setOnFavoriteChangeListener { buttonView, favorite ->
            if(favorite) {
                val editor = getSharedPreferences("com.dev.abhinav.movierater.DetailActivity", MODE_PRIVATE)!!.edit()
                editor.putBoolean("Favorite Added", true)
                editor.apply()
                saveFavorite()
                Snackbar.make(buttonView, "Added to Favorites", Snackbar.LENGTH_SHORT).show()
            } else {
                val movieId = intent.extras!!.getInt("id")
                favoriteDBHelper = FavoriteDBHelper(activity)
                favoriteDBHelper.deleteFavorite(movieId)
                val editor = getSharedPreferences("com.dev.abhinav.movierater.DetailActivity", MODE_PRIVATE)!!.edit()
                editor.putBoolean("Favorite Removed", true)
                editor.apply()
                Snackbar.make(buttonView, "Removed from Favorites", Snackbar.LENGTH_SHORT).show()
            }
        }

        initViews()
    }

    private fun initCollapsingToolbar() {
        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        collapsingToolbar.title = ""
        val appBarLayout: AppBarLayout = findViewById(R.id.appbar)
        appBarLayout.setExpanded(true)

        appBarLayout.addOnOffsetChangedListener(object: AppBarLayout.OnOffsetChangedListener {
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
                    val trailers : List<Trailer> = response.body()!!.getResults()
                    recyclerView.adapter = TrailerAdapter(applicationContext, trailers)
                    recyclerView.smoothScrollToPosition(0)
                }

                override fun onFailure(call: Call<TrailerResponse?>, t: Throwable) {
                    Log.d("Error", t.message)
                    Toast.makeText(this@DetailActivity, "Error fetching trailer data", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e : Exception) {
            Log.d("Error", e.message)
            Toast.makeText(this@DetailActivity, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveFavorite() {
        favoriteDBHelper = FavoriteDBHelper(activity)
        movie = Movie()
        val movieId = intent.extras!!.getInt("id")
        val rate = intent.extras!!.getString("vote_average")
        val poster = intent.extras!!.getString("poster_path")
        movie.setId(movieId)
        movie.setOriginalTitle(nameOfMovie.text.toString().trim())
        movie.setPosterPath(poster!!)
        movie.setVoteAverage(rate!!.toDouble())
        movie.setOverview(plotSynopsis.text.toString().trim())

        //favoriteDBHelper.addFavorite(movie)

    }
}