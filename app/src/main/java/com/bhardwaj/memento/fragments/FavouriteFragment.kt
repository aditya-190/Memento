package com.bhardwaj.memento.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bhardwaj.memento.MainActivity
import com.bhardwaj.memento.adapter.FavouriteAdapter
import com.bhardwaj.memento.databinding.FragmentFavouriteBinding
import com.bhardwaj.memento.models.Favourites

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
        favouriteList.add(Favourites("https://i.redd.it/l3no0vxya9m61.jpg"))
        favouriteList.add(Favourites("https://i.redd.it/n4i3g4uufem61.jpg"))
        favouriteList.add(Favourites("https://i.redd.it/77tekcetbcm61.jpg"))
        favouriteList.add(Favourites("https://i.redd.it/hlljmhkikem61.jpg"))
        favouriteList.add(Favourites("https://i.redd.it/ejfofsf0mcm61.jpg"))
        favouriteList.add(Favourites("https://i.redd.it/ibwc9bc6acm61.jpg"))
        favouriteList.add(Favourites("https://i.redd.it/l3no0vxya9m61.jpg"))
        favouriteList.add(Favourites("https://i.redd.it/l3no0vxya9m61.jpg"))
        favouriteList.add(Favourites("https://i.redd.it/l3no0vxya9m61.jpg"))
        binding.favouriteRecycler.adapter?.notifyDataSetChanged()
    }
}