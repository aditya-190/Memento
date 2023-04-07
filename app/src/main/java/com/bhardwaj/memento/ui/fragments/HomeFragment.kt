package com.bhardwaj.memento.ui.fragments

import android.Manifest
import android.animation.Animator
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bhardwaj.memento.R
import com.bhardwaj.memento.databinding.FragmentHomeBinding
import com.bhardwaj.memento.ui.activity.MainActivity
import com.bhardwaj.memento.viewModels.MainViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null
    private val mainViewModel: MainViewModel by activityViewModels()
    private var isDownload: Boolean = false
    private var rewardedAds: RewardedAd? = null
    private var mediaPlayer: MediaPlayer? = null
    private var readPermissionGranted = false
    private var writePermissionGranted = false
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermissionGranted =
                    permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
                writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermissionGranted

                if (!readPermissionGranted || !writePermissionGranted) {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.permission_denied),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListeners()
        fetchRandomMeme()
    }

    private fun initialiseAds() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            requireContext(),
            getString(R.string.rewarded_id),
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAds = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    rewardedAds = rewardedAd
                    rewardedAds!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {}
                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {}
                        override fun onAdShowedFullScreenContent() {
                            rewardedAds = null
                        }
                    }
                }
            }
        )
    }

    private fun clickListeners() {
        binding?.cvHomeContainer?.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick(v: View?) {
                if (updateOrRequestPermission()) {
                    binding?.lavLikeButton.also { lottie ->
                        mediaPlayer =
                            MediaPlayer.create(requireContext(), R.raw.like_button_clicked)
                        mediaPlayer!!.start()

                        lottie?.alpha = 0.75F
                        lottie?.playAnimation()
                        lottie?.addAnimatorListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {}
                            override fun onAnimationEnd(animation: Animator) {
                                lottie.alpha = 0F
                            }

                            override fun onAnimationCancel(animation: Animator) {}
                            override fun onAnimationRepeat(animation: Animator) {}
                        })
                    }
                    mainViewModel.saveImageToGallery(
                        bitmap = binding?.ivMeme?.drawable?.toBitmap(),
                        fileType = "FAVOURITE"
                    )
                }
            }
        })

        binding?.lavNextMeme?.setOnClickListener {
            fetchRandomMeme()
            mainViewModel.adsCounter += 1
            when (mainViewModel.adsCounter) {
                15 -> {
                    initialiseAds()
                    if (rewardedAds != null) {
                        rewardedAds?.show(activity as MainActivity) {}
                    }
                    mainViewModel.adsCounter = 1
                }
            }

            binding?.lavNextMeme?.speed = 3F
            binding?.lavNextMeme?.playAnimation()

            if (isDownload) {
                binding?.lavDownloadMeme?.speed = -2.5F
                binding?.lavDownloadMeme?.playAnimation()
                isDownload = !isDownload
            }
        }

        binding?.lavDownloadMeme?.setOnClickListener {
            if (updateOrRequestPermission()) {
                if (!isDownload) {
                    mediaPlayer = MediaPlayer.create(requireContext(), R.raw.download_clicked)
                    mediaPlayer!!.start()
                    binding?.lavDownloadMeme?.speed = if (isDownload) -2.5F else 2.5F
                    binding?.lavDownloadMeme?.playAnimation()
                    isDownload = !isDownload
                    mainViewModel.saveImageToGallery(
                        bitmap = binding?.ivMeme?.drawable?.toBitmap(),
                        fileType = "DOWNLOAD"
                    )
                } else {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.downloaded_already),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding?.lavShareButton?.setOnClickListener {
            startActivity(
                Intent().also {
                    it.action = Intent.ACTION_SEND
                    it.type = "text/plain"
                    it.putExtra(
                        Intent.EXTRA_TEXT,
                        "${getString(R.string.share_meme)} -> ${mainViewModel.imageUrl.value}"
                    )
                    Intent.createChooser(it, requireContext().getString(R.string.share_via))
                }
            )
        }

        mainViewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            binding?.ivMeme?.let {
                Glide.with(requireContext()).load(url)
                    .listener(object :
                        com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                        override fun onLoadFailed(
                            e: com.bumptech.glide.load.engine.GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Snackbar.make(
                                requireView(),
                                getString(R.string.something_went_wrong),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            return false
                        }

                        override fun onResourceReady(
                            resource: android.graphics.drawable.Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                            dataSource: com.bumptech.glide.load.DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding?.cpiProgressBar?.visibility = View.GONE
                            binding?.lavDownloadMeme?.isEnabled = true
                            binding?.cvHomeContainer?.isEnabled = true
                            return false
                        }
                    }).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(it)
            }
        }
    }

    private fun fetchRandomMeme() {
        binding?.cpiProgressBar?.visibility = View.VISIBLE
        binding?.lavDownloadMeme?.isEnabled = false
        binding?.cvHomeContainer?.isEnabled = false
        mainViewModel.fetchRandomMeme()
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

    fun updateOrRequestPermission(): Boolean {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val hasWritePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        val minSdk33 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

        readPermissionGranted = hasReadPermission || minSdk33
        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()

        if (!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        return when {
            permissionsToRequest.isEmpty() -> true

            shouldShowRequestPermissionRationale(permissionsToRequest[0]) -> {
                val snackBar = Snackbar.make(
                    requireView(),
                    getString(R.string.permission_required),
                    Snackbar.LENGTH_LONG
                )
                snackBar.show()
                snackBar.setAction(getString(R.string.ok)) {
                    permissionLauncher.launch(permissionsToRequest.toTypedArray())
                }
                false
            }
            else -> {
                permissionLauncher.launch(permissionsToRequest.toTypedArray())
                false
            }
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