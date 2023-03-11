package com.bhardwaj.memento

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.bhardwaj.memento.fragments.DownloadFragment
import com.bhardwaj.memento.fragments.FavouriteFragment
import com.bhardwaj.memento.fragments.HomeFragment
import com.bhardwaj.meowbottomnavigation.MeowBottomNavigation
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var mContext: MainActivity
    private lateinit var clRootMain: ConstraintLayout
    private lateinit var flFragmentContainer: FrameLayout
    private lateinit var mbnNavigation: MeowBottomNavigation
    private lateinit var lavSad: LottieAnimationView
    private lateinit var cvLogOutContainer: CardView
    private lateinit var tvExitHeading: TextView
    private lateinit var tvNoButton: TextView
    private lateinit var tvYesButton: TextView
    private lateinit var avShowAds: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialise()
        clickListeners()
    }

    private fun initialise() {
        mContext = this@MainActivity
        clRootMain = findViewById(R.id.clRootMain)
        flFragmentContainer = findViewById(R.id.flFragmentContainer)
        mbnNavigation = findViewById(R.id.mbnNavigation)
        lavSad = findViewById(R.id.lavSad)
        cvLogOutContainer = findViewById(R.id.cvLogOutContainer)
        tvExitHeading = findViewById(R.id.tvExitHeading)
        tvNoButton = findViewById(R.id.tvNoButton)
        tvYesButton = findViewById(R.id.tvYesButton)
        avShowAds = findViewById(R.id.avShowAds)

        MobileAds.initialize(mContext)
        FirebaseAnalytics.getInstance(mContext)

        avShowAds.loadAd(AdRequest.Builder().build())

        mbnNavigation.apply {
            add(MeowBottomNavigation.Model(0, R.drawable.icon_home))
            add(MeowBottomNavigation.Model(2, R.drawable.icon_favourite))
            add(MeowBottomNavigation.Model(3, R.drawable.icon_download))
            show(0)
        }
        supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, HomeFragment())
            .commit()
    }

    private fun clickListeners() {
        mbnNavigation.setOnClickMenuListener { navigation ->
            val fragment: Fragment = when (navigation.id) {
                2 -> FavouriteFragment().newInstance()
                3 -> DownloadFragment().newInstance()
                else -> HomeFragment().newInstance()
            }
            supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, fragment)
                .commit()
        }
    }

    fun quitDialog() {
        cvLogOutContainer.visibility = View.VISIBLE
        tvExitHeading.visibility = View.VISIBLE
        lavSad.visibility = View.VISIBLE
        tvNoButton.visibility = View.VISIBLE
        tvYesButton.visibility = View.VISIBLE

        tvYesButton.setOnClickListener {
            exitProcess(0)
        }

        tvNoButton.setOnClickListener {
            cvLogOutContainer.visibility = View.GONE
            tvExitHeading.visibility = View.GONE
            lavSad.visibility = View.GONE
            tvNoButton.visibility = View.GONE
            tvYesButton.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        Common.fullScreen(this.window)
    }

    private fun checkWritePermission() = ContextCompat.checkSelfPermission(
        mContext,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    private fun checkReadPermission() = ContextCompat.checkSelfPermission(
        mContext,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    fun requestPermissions() {
        val requestPermissionList = mutableListOf<String>()
        if (!checkReadPermission()) {
            requestPermissionList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (!checkWritePermission()) {
            requestPermissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (requestPermissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                mContext,
                requestPermissionList.toTypedArray(),
                100
            )
        }
    }
}