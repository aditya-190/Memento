package com.bhardwaj.memento.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bhardwaj.memento.R
import com.bhardwaj.memento.data.entity.Downloads
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DownloadAdapter @Inject constructor(
    @ApplicationContext val mContext: Context,
    private val list: ArrayList<Downloads> = arrayListOf(),
) : RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder>() {

    inner class DownloadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.ivMeme)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        return DownloadViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.single_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        val current = list[position]
        Glide.with(mContext).load(current.downloadURI).diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}