package com.bhardwaj.memento.fragments

import android.animation.Animator
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.bhardwaj.memento.Common
import com.bhardwaj.memento.MainActivity
import com.bhardwaj.memento.R
import com.bhardwaj.memento.databinding.FragmentHomeBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.io.OutputStream

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var isDownload: Boolean = false
    private var rewardedAds: RewardedAd? = null
    private var adsCounter: Int = 1

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

    private fun initialiseAds() {
        RewardedAd.load(
            this.context!!,
            getString(R.string.rewarded_id),
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAds = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    rewardedAds = rewardedAd
                }
            })
    }

    private fun showAds() {
        rewardedAds?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
            }

            override fun onAdShowedFullScreenContent() {
                rewardedAds = null
            }
        }

        if (rewardedAds != null) {
            rewardedAds?.show(this@HomeFragment.activity!!) {
            }
        }
    }

    private fun clickListeners() {
        binding.cardView.setOnClickListener(object : Common.DoubleClickListener() {
            override fun onDoubleClick(v: View?) {
                binding.likeButton.also { lottie ->
                    lottie.alpha = 0.75F
                    lottie.playAnimation()
                    lottie.addAnimatorListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator?) {
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
                saveImageToGallery(binding.image.drawable.toBitmap(), "Favourites")
            }
        })

        binding.nextButton.setOnClickListener {
            Common.fetchRandomMeme(binding, this@HomeFragment.activity!!)

            when (adsCounter) {
                1 -> {
                    initialiseAds()
                }

                5 -> {
                    showAds()
                    adsCounter = 1
                }
            }

            binding.nextButton.also {
                it.speed = 3F
                it.playAnimation()
            }

            if (isDownload) {
                binding.downloadButton.also {
                    it.speed = -2.5F
                    it.playAnimation()
                    isDownload = !isDownload
                }
            }
            adsCounter += 1
        }

        binding.downloadButton.setOnClickListener {
            binding.downloadButton.also {
                it.speed = if (isDownload) -2.5F else 2.5F
                it.playAnimation()
                isDownload = !isDownload
            }
            saveImageToGallery(binding.image.drawable.toBitmap(), "Downloads")
        }

        binding.shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "Check this cool meme : ${Common.currentMemeUrl}")
            startActivity(Intent.createChooser(intent, "Share this meme with : "))
        }
    }

    fun saveImageToGallery(bitmap: Bitmap, fileLocation: String) {
        GlobalScope.launch(Dispatchers.IO) {
            (activity as MainActivity).requestPermissions()

            val fileName = String.format("Memento-%d.PNG", System.currentTimeMillis().toInt())
            val filePath = String.format(
                "%s%sMemento%s%s",
                Environment.DIRECTORY_DCIM,
                File.separator,
                File.separator,
                fileLocation
            )

            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/*")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, filePath)

            val resolver = context!!.contentResolver

            val stream: OutputStream?
            var uri: Uri? = null

            try {
                val contentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                uri = resolver.insert(contentUri, contentValues)
                stream = resolver.openOutputStream(uri!!)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream?.close()

            } catch (error: IOException) {
                resolver.delete(uri!!, null, null)
            }
        }
    }
}