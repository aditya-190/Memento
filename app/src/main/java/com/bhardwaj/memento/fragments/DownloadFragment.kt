package com.bhardwaj.memento.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bhardwaj.memento.MainActivity
import com.bhardwaj.memento.adapter.DownloadAdapter
import com.bhardwaj.memento.databinding.FragmentDownloadBinding
import com.bhardwaj.memento.models.Downloads

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
        binding.downloadRecycler.also {
            it.layoutManager = LinearLayoutManager(this@DownloadFragment.activity)
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
        downloadList.add(Downloads("https://i.redd.it/l3no0vxya9m61.jpg"))
        downloadList.add(Downloads("https://i.redd.it/n4i3g4uufem61.jpg"))
        downloadList.add(Downloads("https://i.redd.it/77tekcetbcm61.jpg"))
        downloadList.add(Downloads("https://i.redd.it/hlljmhkikem61.jpg"))
        downloadList.add(Downloads("https://i.redd.it/ejfofsf0mcm61.jpg"))
        downloadList.add(Downloads("https://i.redd.it/ibwc9bc6acm61.jpg"))
        downloadList.add(Downloads("https://i.redd.it/l3no0vxya9m61.jpg"))
        downloadList.add(Downloads("https://i.redd.it/l3no0vxya9m61.jpg"))
        downloadList.add(Downloads("https://i.redd.it/l3no0vxya9m61.jpg"))
        binding.downloadRecycler.adapter?.notifyDataSetChanged()
    }
}