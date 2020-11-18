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
import com.dev.abhinav.movierater.DetailActivity
import com.dev.abhinav.movierater.R
import com.dev.abhinav.movierater.model.Movie

class MoviesAdapter(private val context: Context, private val movieList:  List<Movie>) : RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

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
                    intent.putExtra("original_title", movieList[pos].getOriginalTitle())
                    intent.putExtra("poster_path", movieList[pos].getPosterPath())
                    intent.putExtra("overview", movieList[pos].getOverview())
                    intent.putExtra("vote_average", movieList[pos].getVoteAverage().toString())
                    intent.putExtra("release_date", movieList[pos].getReleaseDate())
                    intent.putExtra("id", movieList[pos].getId())
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    Toast.makeText(itemView.context, "You clicked " + clickedDataItem.getOriginalTitle(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.movie_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = movieList[position].getOriginalTitle()
        val vote = movieList[position].getVoteAverage().toString()
        holder.userrating.text = vote

        Glide.with(context).load(movieList[position].getPosterPath()).placeholder(R.drawable.loading).into(holder.thumbnail)
    }
}