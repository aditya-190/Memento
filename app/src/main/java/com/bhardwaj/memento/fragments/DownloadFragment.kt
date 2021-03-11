package com.bhardwaj.memento.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bhardwaj.memento.R
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
        clickListeners()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun clickListeners() {
        binding.quit.setOnClickListener {
            exitProcess(0)
        }
    }
}