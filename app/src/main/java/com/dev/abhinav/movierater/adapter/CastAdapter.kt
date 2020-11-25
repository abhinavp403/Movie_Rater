package com.dev.abhinav.movierater.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.abhinav.movierater.R
import com.dev.abhinav.movierater.model.Cast

class CastAdapter(private val context: Context, private val castList:  List<Cast>) : RecyclerView.Adapter<CastAdapter.ViewHolder>() {

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name)
        var character: TextView = itemView.findViewById(R.id.character)
        var profilepic: ImageView = itemView.findViewById(R.id.profilepic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cast_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return castList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = castList[position].getName()
        holder.character.text = castList[position].getCharacter()
        Glide.with(context).load(castList[position].getProfilePath()).placeholder(R.drawable.profile).into(holder.profilepic)
    }
}