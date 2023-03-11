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
import com.bhardwaj.memento.adapter.DownloadAdapter
import com.bhardwaj.memento.models.Downloads
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.io.File

class DownloadFragment : Fragment() {
    private lateinit var mContext: Context
    private lateinit var flRootFragmentDownload: FrameLayout
    private lateinit var ivLogOutButton: ImageView
    private lateinit var downloadFilesList: ArrayList<Downloads>
    private lateinit var rvDownloadRecycler: RecyclerView
    private lateinit var downloadAdapter: DownloadAdapter
    private lateinit var lavNoDownloadsLayout: LottieAnimationView

    fun newInstance(): DownloadFragment {
        return DownloadFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_download, container, false)
        initialise(view)
        clickListeners()
        setUpDownloadAdapter()
        fetchData()
        return view
    }

    private fun initialise(view: View) {
        downloadFilesList = ArrayList()
        lavNoDownloadsLayout = view.findViewById(R.id.lavNoDownloadsLayout)
        flRootFragmentDownload = view.findViewById(R.id.flRootFragmentDownload)
        rvDownloadRecycler = view.findViewById(R.id.rvDownloadRecycler)
        ivLogOutButton = view.findViewById(R.id.ivLogOutButton)
    }

    private fun clickListeners() {
        ivLogOutButton.setOnClickListener {
            (activity as MainActivity).quitDialog()
        }
    }

    private fun setUpDownloadAdapter() {
        rvDownloadRecycler.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        downloadAdapter = DownloadAdapter(mContext, downloadFilesList)
        rvDownloadRecycler.adapter = downloadAdapter
        rvDownloadRecycler.overScrollMode = View.OVER_SCROLL_NEVER
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val filePath = File(
                    String.format(
                        "%s%sMemento%sDownloads%s",
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
                        val singleFileDownloads = Downloads(singleFileUri)
                        downloadFilesList.add(singleFileDownloads)
                    }
                }
            } catch (error: Exception) {
                Snackbar.make(
                    flRootFragmentDownload,
                    getString(R.string.something_went_wrong),
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }

            withContext(Dispatchers.Main) {
                lavNoDownloadsLayout.visibility =
                    if (downloadFilesList.isEmpty()) View.VISIBLE else View.GONE
                rvDownloadRecycler.adapter?.notifyDataSetChanged()
            }
        }
    }
}