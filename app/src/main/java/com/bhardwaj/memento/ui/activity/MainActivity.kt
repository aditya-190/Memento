package com.bhardwaj.memento.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import com.bhardwaj.memento.R
import com.bhardwaj.memento.databinding.ActivityMainBinding
import com.bhardwaj.meowbottomnavigation.MeowBottomNavigation
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialise()
        clickListeners()
    }

    private fun initialise() {
        MobileAds.initialize(this@MainActivity)
        FirebaseAnalytics.getInstance(this@MainActivity)
        binding.avShowAds.loadAd(AdRequest.Builder().build())
        binding.mbnNavigation.apply {
            add(MeowBottomNavigation.Model(0, R.drawable.icon_home))
            add(MeowBottomNavigation.Model(1, R.drawable.icon_favourite))
            add(MeowBottomNavigation.Model(2, R.drawable.icon_download))
            show(0)
        }
    }

    private fun clickListeners() {
        binding.mbnNavigation.setOnClickMenuListener { navigation ->
            val navController =
                binding.fNavHost.getFragment<NavHostFragment>().navController
            when (navigation.id) {
                1 -> navController.navigate(R.id.favouriteFragment)
                2 -> navController.navigate(R.id.downloadFragment)
                else -> navController.navigate(R.id.homeFragment)
            }
        }
    }

    fun quitDialog() {
        binding.cvLogOutContainer.visibility = View.VISIBLE
        binding.tvExitHeading.visibility = View.VISIBLE
        binding.lavSad.visibility = View.VISIBLE
        binding.tvNoButton.visibility = View.VISIBLE
        binding.tvYesButton.visibility = View.VISIBLE
        binding.tvYesButton.setOnClickListener { exitProcess(0) }
        binding.tvNoButton.setOnClickListener {
            binding.cvLogOutContainer.visibility = View.GONE
            binding.tvExitHeading.visibility = View.GONE
            binding.lavSad.visibility = View.GONE
            binding.tvNoButton.visibility = View.GONE
            binding.tvYesButton.visibility = View.GONE
        }
    }
}