package com.bhardwaj.memento.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bhardwaj.memento.R
import com.bhardwaj.memento.models.Favourites
import com.bumptech.glide.Glide

class FavouriteAdapter(private var favouriteList: ArrayList<Favourites> = ArrayList()) : RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        return FavouriteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.single_item, parent, false))
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(favouriteList[position].favouriteURI).into(holder.image)
    }

    override fun getItemCount(): Int {
        return favouriteList.size
    }

    inner class FavouriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
    }
}