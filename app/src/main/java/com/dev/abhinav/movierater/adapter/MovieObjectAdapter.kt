package com.dev.abhinav.movierater.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.dev.abhinav.movierater.R
import com.dev.abhinav.movierater.model.MovieDetailObject

class MovieObjectAdapter(private val context: Context, private val movieDetailObjectList: List<MovieDetailObject>) : RecyclerView.Adapter<MovieObjectAdapter.ViewHolder>() {

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.label_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movieDetailObjectList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = movieDetailObjectList[position].getName()
    }
}