package com.bhardwaj.memento.fragments

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bhardwaj.memento.MainActivity
import com.bhardwaj.memento.adapter.FavouriteAdapter
import com.bhardwaj.memento.databinding.FragmentFavouriteBinding
import com.bhardwaj.memento.models.Downloads
import com.bhardwaj.memento.models.Favourites
import java.io.File

class FavouriteFragment : Fragment() {
    private lateinit var binding: FragmentFavouriteBinding
    private var favouriteList: ArrayList<Favourites> = ArrayList()
    private var adapter: FavouriteAdapter = FavouriteAdapter(favouriteList)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initialise()
        clickListeners()
        fetchData()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initialise() {
        binding.favouriteRecycler.also {
            it.layoutManager = LinearLayoutManager(this@FavouriteFragment.activity)
            it.adapter = adapter
            it.overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    private fun clickListeners() {
        binding.quit.setOnClickListener {
            (activity as MainActivity).quitDialog()
        }
    }

    private fun fetchData() {
        favouriteList.add(Favourites(Uri.fromFile(File("/sdcard/DCIM/Memento/Favourites/Memento-625913484.PNG"))))
        favouriteList.add(Favourites(Uri.fromFile(File("/sdcard/DCIM/Memento/Favourites/Memento-626402837.PNG"))))
        favouriteList.add(Favourites(Uri.fromFile(File("/sdcard/DCIM/Memento/Favourites/Memento-626407600.PNG"))))
        binding.favouriteRecycler.adapter?.notifyDataSetChanged()
    }
}