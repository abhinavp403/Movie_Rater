package com.dev.abhinav.movierater

import android.content.Intent
import android.content.SharedPreferences
import android.media.Rating
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration.builder
import com.bumptech.glide.Glide
import com.dev.abhinav.movierater.adapter.CastAdapter
import com.dev.abhinav.movierater.adapter.CrewAdapter
import com.dev.abhinav.movierater.adapter.MovieObjectAdapter
import com.dev.abhinav.movierater.api.Client
import com.dev.abhinav.movierater.api.Service
import com.dev.abhinav.movierater.data.FavoriteDatabase
import com.dev.abhinav.movierater.data.FavoriteList
import com.dev.abhinav.movierater.model.*
import com.github.ivbaranov.mfb.MaterialFavoriteButton
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.DoubleStream.builder
import kotlin.collections.ArrayList

class DetailActivity : AppCompatActivity() {
    private lateinit var nameOfMovie: TextView
    private lateinit var plotSynopsis: TextView
    private lateinit var tagline: TextView
    private lateinit var runtime: TextView
    private lateinit var languages: TextView
    private lateinit var releaseDate: TextView
    private lateinit var imageView: ImageView
    private lateinit var ratingBar: RatingBar
    private lateinit var favoriteDatabase: FavoriteDatabase
    private lateinit var fabMain: FloatingActionButton
    private lateinit var fab1: FloatingActionButton
    private lateinit var fab2: FloatingActionButton
    private var isOpen: Boolean = false
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var fabClock: Animation
    private lateinit var fabAnticlock: Animation
    private lateinit var writeReview: TextView
    private lateinit var rateMovie: TextView

    private lateinit var recyclerViewCast: RecyclerView
    private lateinit var castAdapter: CastAdapter
    private lateinit var castList: List<Cast>

    private lateinit var recyclerViewCrew: RecyclerView
    private lateinit var crewAdapter: CrewAdapter
    private lateinit var crewList: List<Crew>

    private lateinit var recyclerViewGenre: RecyclerView
    private lateinit var movieDetailObjectList: List<MovieDetailObject>
    private lateinit var recyclerViewStudio: RecyclerView
    private lateinit var studioList: List<MovieDetailObject>
    private lateinit var movieObjectAdapter: MovieObjectAdapter

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
        tagline = findViewById(R.id.tagline)
        plotSynopsis = findViewById(R.id.plotsynopsis)
        runtime = findViewById(R.id.runtime)
        languages = findViewById(R.id.language)
        releaseDate = findViewById(R.id.releasedate)
        ratingBar = findViewById(R.id.ratingbar)
        fabMain = findViewById(R.id.fab)
        fab1 = findViewById(R.id.fab1)
        fab2 = findViewById(R.id.fab2)
        fabClose = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)
        fabOpen = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
        fabClock = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_rotate_clock)
        fabAnticlock = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_rotate_anticlock)
        writeReview = findViewById(R.id.write_review)
        rateMovie = findViewById(R.id.rate_movie)

        initFabView()

        initViewsDetails()

        val intent = intent
        if (intent.hasExtra("title")) {
            val thumbnail = intent.extras!!.getString("backdrop_path")
            val movieName = intent.extras!!.getString("title")
            val synopsis = intent.extras!!.getString("overview")
            val dateOfRelease = intent.extras!!.getString("release_date")

            Glide.with(this).load(thumbnail).placeholder(R.drawable.loading).into(imageView)
            nameOfMovie.text = movieName
            plotSynopsis.text = synopsis
            val dateFormat = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault())
            val date = dateFormat.parse(dateOfRelease!!)
            val formatter = SimpleDateFormat("dd MMM yyyy")
            val dateStr = formatter.format(date!!)
            releaseDate.text = dateStr.toString()
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

        loadJSONTrailer()
        initViewsCredits()
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
                    collapsingToolbar.title = intent.extras!!.getString("title")
                    isShow = true
                } else if (isShow) {
                    collapsingToolbar.title = ""
                    isShow = false
                }
            }
        })
    }

    private fun initFabView() {
        fabMain.setOnClickListener {
            isOpen = if (isOpen) {
                writeReview.visibility = View.INVISIBLE
                rateMovie.visibility = View.INVISIBLE
                fab1.startAnimation(fabClose)
                fab2.startAnimation(fabClose)
                fabMain.startAnimation(fabAnticlock)
                fab1.isClickable = false
                fab2.isClickable = false
                false
            } else {
                writeReview.visibility = View.VISIBLE
                rateMovie.visibility = View.VISIBLE
                fab1.startAnimation(fabOpen)
                fab2.startAnimation(fabOpen)
                fabMain.startAnimation(fabClock)
                fab1.isClickable = true
                fab2.isClickable = true
                true
            }
        }

        fab2.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Write Review")
            val dialogLayout: View = layoutInflater.inflate(R.layout.review_dialog, null)
            val textBox = dialogLayout.findViewById<EditText>(R.id.textbox)
            builder.setView(dialogLayout)
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            builder.create()
            builder.show()
        }

        fab1.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Rate Movie")
            val dialogLayout: View = layoutInflater.inflate(R.layout.rate_dialog, null)
            val ratingBar = dialogLayout.findViewById<RatingBar>(R.id.dialog_ratingbar)
            builder.setView(dialogLayout)
            builder.setPositiveButton("OK") { dialog, _ ->
                Toast.makeText(applicationContext, "Rating is " + ratingBar.rating, Toast.LENGTH_SHORT).show()
//                val item = Rating.builder().rate(ratingBar.rating).build()
//                Amplify.DataStore.save(
//                        item,
//                        { success -> Log.i("Amplify", "Saved item: " + success.item().name) },
//                        { error -> Log.e("Amplify", "Could not save item to DataStore", error) }
//                )
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            builder.create()
            builder.show()
        }
    }

    private fun initViewsDetails() {
        recyclerViewGenre = findViewById(R.id.recycler_view_genre)
        movieDetailObjectList = ArrayList()
        movieObjectAdapter = MovieObjectAdapter(this, movieDetailObjectList)
        recyclerViewGenre.layoutManager = LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewGenre.adapter = movieObjectAdapter
        movieObjectAdapter.notifyDataSetChanged()

        recyclerViewStudio = findViewById(R.id.recycler_view_studio)
        studioList = ArrayList()
        movieObjectAdapter = MovieObjectAdapter(this, studioList)
        recyclerViewStudio.layoutManager = LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewStudio.adapter = movieObjectAdapter
        movieObjectAdapter.notifyDataSetChanged()

        loadJSONMovieDetails()
    }

    private fun initViewsCredits() {
        recyclerViewCast = findViewById(R.id.recycler_view_cast)
        castList = ArrayList()
        castAdapter = CastAdapter(this, castList)
        recyclerViewCast.layoutManager = LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewCast.adapter = castAdapter
        castAdapter.notifyDataSetChanged()
        loadJSONCast()

        recyclerViewCrew = findViewById(R.id.recycler_view_crew)
        crewList = ArrayList()
        crewAdapter = CrewAdapter(this, crewList)
        recyclerViewCrew.layoutManager = LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewCrew.adapter = crewAdapter
        crewAdapter.notifyDataSetChanged()
        loadJSONCrew()
    }

    private fun loadJSONTrailer() {
        val movieId = intent.extras!!.getInt("id")
        val title: TextView = findViewById(R.id.trailer_title)
        val trailerCardView: CardView = findViewById(R.id.card_view_trailer)
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
                    val trailer = trailers[0]
                    title.text = trailer.getName()
                    trailerCardView.setOnClickListener {
                        val videoid = trailer.getKey()
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoid"))
                        intent.putExtra("video_id", videoid)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        applicationContext.startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<TrailerResponse?>, t: Throwable) {
                    Log.d("Error", t.message.toString())
                    Toast.makeText(
                        this@DetailActivity,
                        "Error fetching trailer data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (e: Exception) {
            Log.d("Error", e.message.toString())
            Toast.makeText(this@DetailActivity, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadJSONCast() {
        val movieId = intent.extras!!.getInt("id")
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this@DetailActivity, "Please obtain API Key first", Toast.LENGTH_SHORT).show()
                return
            }
            val client = Client()
            val service = client.getClient().create(Service::class.java)
            val call = service.getCredits(movieId, BuildConfig.THE_MOVIE_DB_API_TOKEN)
            call!!.enqueue(object : Callback<CreditResponse?> {
                override fun onResponse(call: Call<CreditResponse?>, response: Response<CreditResponse?>) {
                    val casts: List<Cast> = response.body()!!.getCast()
                    recyclerViewCast.adapter = CastAdapter(applicationContext, casts)
                    recyclerViewCast.smoothScrollToPosition(0)
                }

                override fun onFailure(call: Call<CreditResponse?>, t: Throwable) {
                    Log.d("Error", t.message.toString())
                    Toast.makeText(
                        this@DetailActivity,
                        "Error fetching cast data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (e: Exception) {
            Log.d("Error", e.message.toString())
            Toast.makeText(this@DetailActivity, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadJSONCrew() {
        val movieId = intent.extras!!.getInt("id")
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this@DetailActivity, "Please obtain API Key first", Toast.LENGTH_SHORT).show()
                return
            }
            val client = Client()
            val service = client.getClient().create(Service::class.java)
            val call = service.getCredits(movieId, BuildConfig.THE_MOVIE_DB_API_TOKEN)
            call!!.enqueue(object : Callback<CreditResponse?> {
                override fun onResponse(call: Call<CreditResponse?>, response: Response<CreditResponse?>) {
                    val crews: List<Crew> = response.body()!!.getCrew()
                    recyclerViewCrew.adapter = CrewAdapter(applicationContext, crews)
                    recyclerViewCrew.smoothScrollToPosition(0)
                }

                override fun onFailure(call: Call<CreditResponse?>, t: Throwable) {
                    Log.d("Error", t.message.toString())
                    Toast.makeText(this@DetailActivity, "Error fetching crew data", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Log.d("Error", e.message.toString())
            Toast.makeText(this@DetailActivity, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadJSONMovieDetails() {
        val movieId = intent.extras!!.getInt("id")
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this@DetailActivity, "Please obtain API Key first", Toast.LENGTH_SHORT).show()
                return
            }
            val client = Client()
            val service = client.getClient().create(Service::class.java)
            val call = service.getMovieDetails(movieId, BuildConfig.THE_MOVIE_DB_API_TOKEN)
            call!!.enqueue(object : Callback<MovieDetailResponse?> {
                override fun onResponse(call: Call<MovieDetailResponse?>, response: Response<MovieDetailResponse?>) {
                    tagline.text = response.body()!!.getTagline()
                    runtime.text = response.body()!!.getRuntime().toString() + " mins"
                    if (response.body()!!.getTagline() == "") {
                        tagline.visibility = View.GONE
                    }
                    if (response.body()!!.getRuntime() == 0) {
                        runtime.visibility = View.GONE
                    }
                    val movieDetailObjects: List<MovieDetailObject> = response.body()!!.getGenres()
                    val studios: List<MovieDetailObject> = response.body()!!.getStudios()
                    val spokenLanguages: List<MovieDetailObject> = response.body()!!.getSpokenLanguages()
                    var lang = ""
                    for (language in spokenLanguages.indices) {
                        lang = if(language == spokenLanguages.size - 1) {
                            lang + spokenLanguages[language].getLanguage()
                        } else {
                            lang + spokenLanguages[language].getLanguage() + ", "
                        }
                    }
                    languages.text = lang
                    recyclerViewGenre.adapter = MovieObjectAdapter(applicationContext, movieDetailObjects)
                    recyclerViewStudio.adapter = MovieObjectAdapter(applicationContext, studios)
                    recyclerViewGenre.smoothScrollToPosition(0)
                    recyclerViewStudio.smoothScrollToPosition(0)
                }

                override fun onFailure(call: Call<MovieDetailResponse?>, t: Throwable) {
                    Log.d("Error", t.message.toString())
                    Toast.makeText(
                        this@DetailActivity, "Error fetching movie detail data", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Log.d("Error", e.message.toString())
            Toast.makeText(this@DetailActivity, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveFavorite() {
        val favoriteList = FavoriteList()
        favoriteList.movieid = intent.extras!!.getInt("id")
        favoriteList.mtitle = nameOfMovie.text.toString().trim()
        favoriteList.posterpath = intent.extras!!.getString("backdrop_path")
        favoriteList.overview = plotSynopsis.text.toString().trim()
        favoriteList.releasedate = releaseDate.text.toString().trim()
        favoriteDatabase.favoriteDao().insert(favoriteList)
    }

    private fun removeFavorite() {
        val favoriteList = FavoriteList()
        favoriteList.movieid = intent.extras!!.getInt("id")
        favoriteList.mtitle = nameOfMovie.text.toString().trim()
        favoriteList.posterpath = intent.extras!!.getString("backdrop_path")
        favoriteList.overview = plotSynopsis.text.toString().trim()
        favoriteList.releasedate = releaseDate.text.toString().trim()
        favoriteDatabase.favoriteDao().delete(favoriteList)
    }
}