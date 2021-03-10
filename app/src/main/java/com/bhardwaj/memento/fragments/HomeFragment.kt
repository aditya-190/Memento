package com.bhardwaj.memento.fragments

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bhardwaj.memento.MySingleton
import com.bhardwaj.memento.databinding.FragmentHomeBinding
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentMemeUrl: String
    private var isDownload: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        clickListeners()
        fetchRandomMeme()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun clickListeners() {

        binding.cardView.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick(v: View?) {
                binding.likeButton.also { lottie ->

                    lottie.playAnimation()
                    lottie.addAnimatorListener(object : AnimatorListener {
                        override fun onAnimationStart(animation: Animator?) {
                            lottie.alpha = 0.7F
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            lottie.alpha = 0F
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationRepeat(animation: Animator?) {
                        }
                    })
                }
            }
        })

        binding.nextButton.setOnClickListener {
            fetchRandomMeme()
            binding.nextButton.also {
                it.speed = 3F
                it.playAnimation()
            }
        }

        binding.downloadButton.setOnClickListener {
            binding.downloadButton.also {
                it.speed = if (isDownload) -2.5F else 2.5F
                it.playAnimation()
                isDownload = !isDownload
            }

            CoroutineScope(Dispatchers.IO).launch {
                TODO()
            }
        }

        binding.shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "Check this cool meme : $currentMemeUrl")
            startActivity(Intent.createChooser(intent, "Share this meme with : "))
        }
    }

    private fun fetchRandomMeme() {
        binding.progressBar.visibility = View.VISIBLE
        val url = "https://meme-api.herokuapp.com/gimme"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, { response ->
                currentMemeUrl = response.getString("url")
                Glide.with(this).load(currentMemeUrl).into(binding.image)
                binding.progressBar.visibility = View.GONE
            }, {
                binding.progressBar.visibility = View.GONE
            })
        MySingleton.getInstance(this.activity!!).addToRequestQueue(jsonObjectRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

abstract class DoubleClickListener : View.OnClickListener {
    private var lastClickTime: Long = 0
    override fun onClick(v: View?) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v)
        }
        lastClickTime = clickTime
    }

    abstract fun onDoubleClick(v: View?)

    companion object {
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300
    }
}