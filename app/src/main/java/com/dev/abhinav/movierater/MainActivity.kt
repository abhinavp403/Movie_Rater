package com.dev.abhinav.movierater

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dev.abhinav.movierater.adapter.MoviesAdapter
import com.dev.abhinav.movierater.api.Client
import com.dev.abhinav.movierater.api.Service
import com.dev.abhinav.movierater.data.FavoriteDatabase
import com.dev.abhinav.movierater.model.Movie
import com.dev.abhinav.movierater.model.MoviesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : MoviesAdapter
    private lateinit var movieList : MutableList<Movie>
    private lateinit var progressDialog : ProgressDialog
    private lateinit var swipeContainer : SwipeRefreshLayout
    private lateinit var favoriteDatabase: FavoriteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeContainer = findViewById(R.id.main_content)
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark)
        swipeContainer.setOnRefreshListener {
            initViews()
            Toast.makeText(this@MainActivity, "Movies Refreshed", Toast.LENGTH_SHORT).show()
        }

        initViews()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Fetching movies...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        recyclerView = findViewById(R.id.recycler_view)
        movieList = ArrayList()
        adapter = MoviesAdapter(this, movieList)

        if(getActivity()!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.layoutManager = GridLayoutManager(this, 2)
        } else {
            recyclerView.layoutManager = GridLayoutManager(this, 4)
        }
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        //favoriteDatabase = FavoriteDatabase.getInstance(applicationContext)
        //favoriteDatabase.favoriteDao().deleteAll()
        //getAllFavorite()
        checkSortOrder()
    }

    private fun initViews2() {
        recyclerView = findViewById(R.id.recycler_view)
        movieList = ArrayList()
        adapter = MoviesAdapter(this, movieList)

        if(getActivity()!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.layoutManager = GridLayoutManager(this, 2)
        } else {
            recyclerView.layoutManager = GridLayoutManager(this, 4)
        }
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        favoriteDatabase = FavoriteDatabase.getInstance(applicationContext)
        getAllFavorite()
    }

    @SuppressLint("StaticFieldLeak")
    private fun getAllFavorite() {
        object : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg params: Void?): Void? {
                movieList.clear()
                val favoriteList = favoriteDatabase.favoriteDao().getAll()
                Log.d("abc", favoriteList.toString())
                for (faves in favoriteList) {
                    val movie = Movie()
                    faves.movieid?.let { movie.setId(it) }
                    faves.mtitle?.let { movie.setTitle(it) }
                    faves.posterpath?.let { movie.setPosterPath(it) }
                    faves.overview?.let { movie.setOverview(it) }
                    faves.releasedate?.let { movie.setReleaseDate(it) }
                    movieList.add(movie)
                }
                Log.d("abc2", favoriteList.toString())
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                adapter.notifyDataSetChanged()
            }
        }.execute()
    }

    private fun getActivity(): Activity? {
        var context: Context = this
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

    private fun loadJSON() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this@MainActivity, "Please obtain API Key first", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
                return
            }
            val client = Client()
            val service = client.getClient().create(Service::class.java)
            val call = service.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN)
            call!!.enqueue(object : Callback<MoviesResponse?> {
                override fun onResponse(call: Call<MoviesResponse?>, response: Response<MoviesResponse?>) {
                    val movies: List<Movie> = response.body()!!.getResults()
                    recyclerView.adapter = MoviesAdapter(applicationContext, movies)
                    recyclerView.smoothScrollToPosition(0)
                    if (swipeContainer.isRefreshing) {
                        swipeContainer.isRefreshing = false
                    }
                    progressDialog.dismiss()
                }

                override fun onFailure(call: Call<MoviesResponse?>, t: Throwable) {
                    Log.d("Error", t.message.toString())
                    Toast.makeText(this@MainActivity, "Error fetching data", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (e: Exception) {
            Log.d("Error", e.message.toString())
            Toast.makeText(this@MainActivity, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadJSON2() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this@MainActivity, "Please obtain API Key first", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
                return
            }
            val client = Client()
            val service = client.getClient().create(Service::class.java)
            val call = service.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN)
            call!!.enqueue(object : Callback<MoviesResponse?> {
                override fun onResponse(call: Call<MoviesResponse?>, response: Response<MoviesResponse?>) {
                    val movies: List<Movie> = response.body()!!.getResults()
                    recyclerView.adapter = MoviesAdapter(applicationContext, movies)
                    recyclerView.smoothScrollToPosition(0)
                    if (swipeContainer.isRefreshing) {
                        swipeContainer.isRefreshing = false
                    }
                    progressDialog.dismiss()
                }

                override fun onFailure(call: Call<MoviesResponse?>, t: Throwable) {
                    Log.d("Error", t.message.toString())
                    Toast.makeText(this@MainActivity, "Error fetching data", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (e: Exception) {
            Log.d("Error", e.message.toString())
            Toast.makeText(this@MainActivity, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, s: String?) {
        checkSortOrder()
    }

    private fun checkSortOrder() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        when (preferences.getString(
            this.getString(R.string.pref_sort_order_key),
            this.getString(R.string.pref_most_popular)
        )) {
            this.getString(R.string.pref_most_popular) -> {
                loadJSON()
            }
            this.getString(R.string.favorite) -> {
                initViews2()
            }
            else -> {
                loadJSON2()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(movieList.isEmpty()) {
            checkSortOrder()
        }
    }
}