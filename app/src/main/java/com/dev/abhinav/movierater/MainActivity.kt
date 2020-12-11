package com.dev.abhinav.movierater

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : MoviesAdapter
    private lateinit var movieList : MutableList<Movie>
    private lateinit var progressDialog : ProgressDialog
    private lateinit var swipeContainer : SwipeRefreshLayout
    private lateinit var favoriteDatabase: FavoriteDatabase
    private var cacheSize: Int = 10*1024*1024 //10mb

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
                for (faves in favoriteList) {
                    val movie = Movie()
                    faves.movieid?.let { movie.setId(it) }
                    faves.mtitle?.let { movie.setTitle(it) }
                    faves.posterpath?.let { movie.setPosterPath(it) }
                    faves.overview?.let { movie.setOverview(it) }
                    faves.releasedate?.let { movie.setReleaseDate(it) }
                    movieList.add(movie)
                }
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

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun loadJSON() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(this@MainActivity, "Please obtain API Key first", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
                return
            }
            val cache = Cache(cacheDir, cacheSize.toLong())
            val okHttpClient = OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(object: Interceptor {
                        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                            var request = chain.request()
                            if(!isNetworkAvailable()) {
                                val maxStale = 60*60*24*28
                                request = request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=$maxStale").build()
                            }
                            return chain.proceed(request)
                        }
                    })
                    .build()

            val retrofit = Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/3/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            val service = retrofit.create(Service::class.java)
//            val client = Client()
//            val service = client.getClient().create(Service::class.java)
            val movies = arrayListOf<Movie>()
            val map: HashMap<String?, String?> = HashMap()
            val pageList = mutableListOf<String>()
            for( i in 1..500) {
                pageList.add(i.toString())
            }
            for (page in pageList) {
                map["api_key"] = BuildConfig.THE_MOVIE_DB_API_TOKEN
                map["page"] = page
                val call2 = service.getPopularMovies2(map)
                call2!!.enqueue(object : Callback<MoviesResponse?> {
                    override fun onResponse(call: Call<MoviesResponse?>, response: Response<MoviesResponse?>) {
                        for (movie in response.body()!!.getResults()) {
                            movies.add(movie)
                        }
                        recyclerView.adapter = MoviesAdapter(applicationContext, movies)
                        recyclerView.smoothScrollToPosition(0)
                        if (swipeContainer.isRefreshing) {
                            swipeContainer.isRefreshing = false
                        }
                        progressDialog.dismiss()
                    }

                    override fun onFailure(call: Call<MoviesResponse?>, t: Throwable) {
                        Log.d("Error", t.message.toString())
                        Toast.makeText(this@MainActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
                    }
                })
            }
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
                    Toast.makeText(this@MainActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
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
        when (preferences.getString(this.getString(R.string.pref_sort_order_key), this.getString(R.string.pref_most_popular))) {
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