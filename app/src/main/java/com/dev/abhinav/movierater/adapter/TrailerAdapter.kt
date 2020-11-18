package com.dev.abhinav.movierater.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.dev.abhinav.movierater.R
import com.dev.abhinav.movierater.model.Trailer

class TrailerAdapter(private val context: Context, private val trailerList:  List<Trailer>) : RecyclerView.Adapter<TrailerAdapter.ViewHolder>() {

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.trailer_title)
        var thumbnail: ImageView = itemView.findViewById(R.id.trailer_iv)

        init {
            itemView.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    val clickedDataItem = trailerList[pos]
                    val videoid = trailerList[pos].getKey()
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoid"))
                    intent.putExtra("video_id", videoid)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent)
                    Toast.makeText(itemView.context, "You clicked " + clickedDataItem.getName(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trailer_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trailerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("abc", trailerList[position].getName())
        holder.title.text = trailerList[position].getName()
    }
}