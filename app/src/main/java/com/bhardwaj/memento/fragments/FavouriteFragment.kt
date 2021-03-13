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
import com.bhardwaj.memento.models.Favourites
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        binding.favouriteRecycler.also { recycler ->
            recycler.layoutManager = LinearLayoutManager(this@FavouriteFragment.activity).also {
                it.stackFromEnd = true
            }
            recycler.adapter = adapter
            recycler.overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    private fun clickListeners() {
        binding.quit.setOnClickListener {
            (activity as MainActivity).quitDialog()
        }
    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.IO) {
            val filePath = File(
                String.format(
                    "%s%sMemento%sFavourites%s",
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                        .toString(),
                    File.separator,
                    File.separator,
                    File.separator
                )
            )
            val allFiles = filePath.listFiles()

            if (!allFiles.isNullOrEmpty()) {
                for (i in allFiles) {
                    favouriteList.add(Favourites(Uri.fromFile(File(filePath.absolutePath + File.separator + i.name))))
                }
                binding.favouriteRecycler.scrollToPosition(allFiles.size - 1)
            }
            withContext(Dispatchers.Main) {
                binding.nothing.visibility =
                    if (favouriteList.isEmpty()) View.VISIBLE else View.GONE
                binding.favouriteRecycler.adapter?.notifyDataSetChanged()
            }
        }
    }
}