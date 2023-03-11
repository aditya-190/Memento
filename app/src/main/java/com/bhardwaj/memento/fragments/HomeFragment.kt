package com.bhardwaj.memento.fragments

import android.animation.Animator
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bhardwaj.memento.Common.Companion.adsCounter
import com.bhardwaj.memento.MainActivity
import com.bhardwaj.memento.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.io.OutputStream

class HomeFragment : Fragment() {
    private lateinit var mContext: Context
    private lateinit var flRootFragmentHome: FrameLayout
    private lateinit var lavShareButton: LottieAnimationView
    private lateinit var lavLikeButton: LottieAnimationView
    private lateinit var lavDownloadMeme: LottieAnimationView
    private lateinit var lavNextMeme: LottieAnimationView
    private lateinit var cpiProgressBar: CircularProgressIndicator
    private lateinit var cvHomeContainer: CardView
    private lateinit var ivMeme: ImageView
    private var currentMemeUrl: String = ""
    private var mementoInstance: MementoInstance? = null
    private var isDownload: Boolean = false
    private var rewardedAds: RewardedAd? = null
    private var mediaPlayer: MediaPlayer? = null

    fun newInstance(): HomeFragment {
        return HomeFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initialise(view)
        clickListeners()
        fetchRandomMeme()
        return view
    }

    private fun initialise(view: View) {
        mementoInstance = MementoInstance.instance
        flRootFragmentHome = view.findViewById(R.id.flRootFragmentHome)
        lavShareButton = view.findViewById(R.id.lavShareButton)
        lavLikeButton = view.findViewById(R.id.lavLikeButton)
        lavDownloadMeme = view.findViewById(R.id.lavDownloadMeme)
        lavNextMeme = view.findViewById(R.id.lavNextMeme)
        cpiProgressBar = view.findViewById(R.id.cpiProgressBar)
        cvHomeContainer = view.findViewById(R.id.cvHomeContainer)
        ivMeme = view.findViewById(R.id.ivMeme)
    }

    private fun initialiseAds() {
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            mContext,
            getString(R.string.rewarded_id),
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAds = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    rewardedAds = rewardedAd

                    rewardedAds!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        }

                        override fun onAdShowedFullScreenContent() {
                            rewardedAds = null
                        }
                    }
                }
            }
        )
    }

    private fun clickListeners() {
        cvHomeContainer.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick(v: View?) {
                lavLikeButton.also { lottie ->
                    mediaPlayer = MediaPlayer.create(mContext, R.raw.like_button_clicked)
                    mediaPlayer!!.start()

                    lottie.alpha = 0.75F
                    lottie.playAnimation()
                    lottie.addAnimatorListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            lottie.alpha = 0F
                        }

                        override fun onAnimationCancel(animation: Animator) {
                        }

                        override fun onAnimationRepeat(animation: Animator) {
                        }
                    })
                }
                saveImageToGallery(ivMeme.drawable.toBitmap(), "Favourites")
            }
        })

        lavNextMeme.setOnClickListener {
            fetchRandomMeme()
            adsCounter += 1
            when (adsCounter) {
                4 -> {
                    initialiseAds()
                    if (rewardedAds != null) {
                        rewardedAds?.show(activity as MainActivity) {}
                    }
                    adsCounter = 1
                }
            }

            lavNextMeme.speed = 3F
            lavNextMeme.playAnimation()

            if (isDownload) {
                lavDownloadMeme.speed = -2.5F
                lavDownloadMeme.playAnimation()
                isDownload = !isDownload
            }
        }

        lavDownloadMeme.setOnClickListener {
            if (!isDownload) {
                mediaPlayer = MediaPlayer.create(mContext, R.raw.download_clicked)
                mediaPlayer!!.start()
                lavDownloadMeme.speed = if (isDownload) -2.5F else 2.5F
                lavDownloadMeme.playAnimation()
                isDownload = !isDownload
                saveImageToGallery(ivMeme.drawable.toBitmap(), "Downloads")
            } else {
                Snackbar.make(
                    flRootFragmentHome,
                    getString(R.string.downloaded_already),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        lavShareButton.setOnClickListener {
            startActivity(
                Intent().also {
                    it.action = Intent.ACTION_SEND
                    it.type = "text/plain"
                    it.putExtra(Intent.EXTRA_TEXT, "Check This Meme: $currentMemeUrl")
                    Intent.createChooser(it, mContext.getString(R.string.share_via))
                }
            )
        }
    }

    private fun fetchRandomMeme() {
        cpiProgressBar.visibility = View.VISIBLE
        lavDownloadMeme.isEnabled = false
        cvHomeContainer.isEnabled = false

        val url = "https://meme-api.com/gimme"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null, { response ->
                currentMemeUrl = response.getString("url")
                Glide.with(mContext).load(currentMemeUrl)
                    .listener(object :
                        com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                        override fun onLoadFailed(
                            e: com.bumptech.glide.load.engine.GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            fetchRandomMeme()
                            return false
                        }

                        override fun onResourceReady(
                            resource: android.graphics.drawable.Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                            dataSource: com.bumptech.glide.load.DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            lavDownloadMeme.isEnabled = true
                            cvHomeContainer.isEnabled = true
                            cpiProgressBar.visibility = View.GONE
                            return false
                        }
                    }).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(ivMeme)
            }, {
                cpiProgressBar.visibility = View.GONE
            })
        mementoInstance?.addToRequestQueue(request)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun saveImageToGallery(bitmap: Bitmap, fileLocation: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, filePath)
                }

                val resolver = mContext.contentResolver

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
            } catch (error: Exception) {
                Snackbar.make(
                    flRootFragmentHome,
                    getString(R.string.something_went_wrong),
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
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

    override fun onStop() {
        super.onStop()
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
}