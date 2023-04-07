package com.bhardwaj.memento.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bhardwaj.memento.databinding.FragmentDownloadBinding
import com.bhardwaj.memento.ui.activity.MainActivity
import com.bhardwaj.memento.ui.adapter.DownloadAdapter
import com.bhardwaj.memento.viewModels.MainViewModel

class DownloadFragment : Fragment() {
    private var binding: FragmentDownloadBinding? = null
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDownloadBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.ivLogOutButton?.setOnClickListener {
            (activity as MainActivity).quitDialog()
        }
        mainViewModel.getData(from = "DOWNLOAD")
        mainViewModel.imageListFromDownloads.observe(viewLifecycleOwner) { imageList ->
            binding?.rvDownloadRecycler.also {
                it?.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                it?.adapter = DownloadAdapter(requireContext(), imageList)
                it?.overScrollMode = View.OVER_SCROLL_NEVER
            }
            binding?.lavNoDownloadsLayout?.visibility =
                if (imageList.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}