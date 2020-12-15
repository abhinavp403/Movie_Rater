package com.dev.abhinav.movierater.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.dev.abhinav.movierater.DetailActivity
import com.dev.abhinav.movierater.R
import com.dev.abhinav.movierater.SettingsActivity
import com.dev.abhinav.movierater.model.Movie

class MoviesAdapter(private val context: Context, private val movieList: List<Movie>) : RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    private var item = 0
    private var loading = 1
    private var BASE_URL_IMG = "https://image.tmdb.org/t/p/w150"
    //"http://image.tmdb.org/t/p/w500$posterPath"
    private var isLoadingAdded = false

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.title)
        var userrating: TextView = itemView.findViewById(R.id.userrating)
        var thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)

        init {
            itemView.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    val clickedDataItem = movieList[pos]
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra("title", movieList[pos].getTitle())
                    intent.putExtra("backdrop_path", movieList[pos].getBackdropPath())
                    intent.putExtra("overview", movieList[pos].getOverview())
                    intent.putExtra("release_date", movieList[pos].getReleaseDate())
                    intent.putExtra("id", movieList[pos].getId())
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    Toast.makeText(itemView.context, "You clicked " + clickedDataItem.getTitle(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        var viewHolder: RecyclerView.ViewHolder? = null
//        var inflater = LayoutInflater.from(context)
//        when(viewType) {
//            item -> {
//                viewHolder = getViewHolder(parent, inflater)
//            }
//            loading -> {
//                var view2 = inflater.inflate(R.layout.movie_card, parent, false)
//                viewHolder = LoadingVH(view2)
//            }
//        }
//        return viewHolder
        val view = LayoutInflater.from(context).inflate(R.layout.movie_card, parent, false)
        return ViewHolder(view)
    }

//    fun getViewHolder(parent: ViewGroup, inflater: LayoutInflater): RecyclerView.ViewHolder {
//        var viewHolder: RecyclerView.ViewHolder? = null
//        var view1 = inflater.inflate(R.layout.movie_card, parent, false)
//        return viewHolder
//    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = movieList[position].getTitle()
        val vote = movieList[position].getVoteAverage().toString()
        holder.userrating.text = vote
        Glide.with(context).load(movieList[position].getPosterPath())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.loading).into(holder.thumbnail)
    }
}