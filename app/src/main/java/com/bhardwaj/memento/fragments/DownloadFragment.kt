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
import com.bhardwaj.memento.adapter.DownloadAdapter
import com.bhardwaj.memento.databinding.FragmentDownloadBinding
import com.bhardwaj.memento.models.Downloads
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DownloadFragment : Fragment() {
    private lateinit var binding: FragmentDownloadBinding
    private var downloadList: ArrayList<Downloads> = ArrayList()
    private var adapter: DownloadAdapter = DownloadAdapter(downloadList)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initialise()
        clickListeners()
        fetchData()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initialise() {
        binding.downloadRecycler.also { recycler ->
            recycler.layoutManager = LinearLayoutManager(this@DownloadFragment.activity).also {
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
            val filePath = File(String.format("%s%sMemento%sDownloads%s", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString(), File.separator, File.separator, File.separator))
            val allFiles = filePath.listFiles()

            if (!allFiles.isNullOrEmpty()) {
                for (i in allFiles) {
                    downloadList.add(Downloads(Uri.fromFile(File(filePath.absolutePath + File.separator + i.name))))
                }
                binding.downloadRecycler.scrollToPosition(allFiles.size - 1)
            }

            withContext(Dispatchers.Main) {
                binding.nothing.visibility = if (downloadList.isEmpty()) View.VISIBLE else View.GONE
                binding.downloadRecycler.adapter?.notifyDataSetChanged()

            }
        }
    }
}