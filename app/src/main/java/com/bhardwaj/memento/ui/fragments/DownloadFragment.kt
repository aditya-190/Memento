package com.bhardwaj.memento.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bhardwaj.memento.data.entity.Downloads
import com.bhardwaj.memento.databinding.FragmentDownloadBinding
import com.bhardwaj.memento.ui.activity.MainActivity
import com.bhardwaj.memento.ui.adapter.DownloadAdapter
import com.bhardwaj.memento.viewModels.MainViewModel

class DownloadFragment : Fragment() {
    private var _binding: FragmentDownloadBinding? = null
    private val binding get() = _binding!!
    private var list = arrayListOf<Downloads>()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivLogOutButton.setOnClickListener {
            (activity as MainActivity).quitDialog()
        }
        binding.rvDownloadRecycler.also {
            it.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            it.adapter = DownloadAdapter(requireContext(), list)
            it.overScrollMode = View.OVER_SCROLL_NEVER
        }
        mainViewModel.getData(list = list as ArrayList<Any>, from = "DOWNLOAD")
        binding.lavNoDownloadsLayout.visibility =
            if (list.isEmpty()) View.VISIBLE else View.GONE
        binding.rvDownloadRecycler.adapter?.notifyDataSetChanged()
    }
}