package com.bhardwaj.memento.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bhardwaj.memento.MainActivity
import com.bhardwaj.memento.databinding.FragmentDownloadBinding
import kotlin.system.exitProcess

class DownloadFragment : Fragment() {
    private lateinit var binding: FragmentDownloadBinding

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
    }

    private fun clickListeners() {
        binding.quit.setOnClickListener {
            (activity as MainActivity).quitDialog()
        }
    }


    private fun fetchData() {
    }
}