package com.bhardwaj.memento.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bhardwaj.memento.data.entity.Favourites
import com.bhardwaj.memento.databinding.FragmentFavouriteBinding
import com.bhardwaj.memento.ui.activity.MainActivity
import com.bhardwaj.memento.ui.adapter.FavouriteAdapter
import com.bhardwaj.memento.viewModels.MainViewModel

class FavouriteFragment : Fragment() {
    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!
    private var list = arrayListOf<Favourites>()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivLogOutButton.setOnClickListener {
            (activity as MainActivity).quitDialog()
        }
        binding.rvFavouriteRecycler.also {
            it.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            it.adapter = FavouriteAdapter(requireContext(), list)
            it.overScrollMode = View.OVER_SCROLL_NEVER
        }
        mainViewModel.getData(list = list as ArrayList<Any>, from = "FAVOURITE")
        binding.lavNoFavouritesLayout.visibility =
            if (list.isEmpty()) View.VISIBLE else View.GONE
        binding.rvFavouriteRecycler.adapter?.notifyDataSetChanged()
    }
}