package com.bhardwaj.memento.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bhardwaj.memento.R
import com.bhardwaj.memento.data.entity.Favourites
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class FavouriteAdapter @Inject constructor(
    @ApplicationContext val mContext: Context,
    private var list: ArrayList<Favourites> = arrayListOf()
) : RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {

    inner class FavouriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.ivMeme)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        return FavouriteViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.single_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val current = list[position]
        Glide.with(mContext).load(current.favouriteURI).diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}