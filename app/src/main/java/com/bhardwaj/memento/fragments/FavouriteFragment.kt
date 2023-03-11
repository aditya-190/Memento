package com.bhardwaj.memento.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bhardwaj.memento.MainActivity
import com.bhardwaj.memento.R
import com.bhardwaj.memento.adapter.FavouriteAdapter
import com.bhardwaj.memento.models.Favourites
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.io.File

class FavouriteFragment : Fragment() {
    private lateinit var mContext: Context
    private lateinit var flRootFragmentFavourite: FrameLayout
    private lateinit var ivLogOutButton: ImageView
    private lateinit var favouriteFilesList: ArrayList<Favourites>
    private lateinit var rvFavouriteRecycler: RecyclerView
    private lateinit var favouriteAdapter: FavouriteAdapter
    private lateinit var lavNoFavouriteLayout: LottieAnimationView

    fun newInstance(): FavouriteFragment {
        return FavouriteFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        initialise(view)
        clickListeners()
        setUpFavouriteAdapter()
        fetchData()
        return view
    }

    private fun initialise(view: View) {
        favouriteFilesList = ArrayList()
        lavNoFavouriteLayout = view.findViewById(R.id.lavNoFavouritesLayout)
        flRootFragmentFavourite = view.findViewById(R.id.flRootFragmentFavourite)
        rvFavouriteRecycler = view.findViewById(R.id.rvFavouriteRecycler)
        ivLogOutButton = view.findViewById(R.id.ivLogOutButton)
    }

    private fun clickListeners() {
        ivLogOutButton.setOnClickListener {
            (activity as MainActivity).quitDialog()
        }
    }

    private fun setUpFavouriteAdapter() {
        rvFavouriteRecycler.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        favouriteAdapter = FavouriteAdapter(mContext, favouriteFilesList)
        rvFavouriteRecycler.adapter = favouriteAdapter
        rvFavouriteRecycler.overScrollMode = View.OVER_SCROLL_NEVER
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
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
                        val singleFile = File(filePath.absolutePath + File.separator + i.name)
                        val singleFileUri = Uri.fromFile(singleFile)
                        val singleFileDownloads = Favourites(singleFileUri)
                        favouriteFilesList.add(singleFileDownloads)
                    }
                }
            } catch (error: Exception) {
                Snackbar.make(
                    flRootFragmentFavourite,
                    getString(R.string.something_went_wrong),
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }

            withContext(Dispatchers.Main) {
                lavNoFavouriteLayout.visibility =
                    if (favouriteFilesList.isEmpty()) View.VISIBLE else View.GONE
                rvFavouriteRecycler.adapter?.notifyDataSetChanged()
            }
        }
    }
}