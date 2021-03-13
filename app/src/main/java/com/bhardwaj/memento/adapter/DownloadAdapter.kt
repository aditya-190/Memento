package com.bhardwaj.memento.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bhardwaj.memento.R
import com.bhardwaj.memento.models.Downloads
import com.bumptech.glide.Glide

class DownloadAdapter(private var downloadList: ArrayList<Downloads> = ArrayList()) : RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        return DownloadViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.single_item, parent, false))
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(downloadList[position].downloadURI).into(holder.image)
    }

    override fun getItemCount(): Int {
        return downloadList.size
    }

    inner class DownloadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
    }
}