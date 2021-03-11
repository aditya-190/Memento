package com.bhardwaj.memento.fragments

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bhardwaj.memento.Common
import com.bhardwaj.memento.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var isDownload: Boolean = false

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        clickListeners()
        Common.fetchRandomMeme(binding, this@HomeFragment.activity!!)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun clickListeners() {
        binding.cardView.setOnClickListener(object : Common.DoubleClickListener() {
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
            Common.fetchRandomMeme(binding, this@HomeFragment.activity!!)
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
        }

        binding.shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "Check this cool meme : ${Common.currentMemeUrl}")
            startActivity(Intent.createChooser(intent, "Share this meme with : "))
        }
    }
}