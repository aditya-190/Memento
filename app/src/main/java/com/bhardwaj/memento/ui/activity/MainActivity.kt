package com.bhardwaj.memento.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import com.bhardwaj.memento.R
import com.bhardwaj.memento.databinding.ActivityMainBinding
import com.bhardwaj.meowbottomnavigation.MeowBottomNavigation
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var readPermissionGranted = false
    private var writePermissionGranted = false
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

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
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermissionGranted =
                    permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
                writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermissionGranted

                if (!readPermissionGranted || !writePermissionGranted) {
                    Snackbar.make(
                        binding.clRootMain,
                        getString(R.string.permission_denied),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun clickListeners() {
        binding.mbnNavigation.setOnClickMenuListener { navigation ->
            val navController =
                binding.flFragmentContainer.getFragment<NavHostFragment>().navController
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

    fun updateOrRequestPermission(): Boolean {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val hasWritePermission = ContextCompat.checkSelfPermission(
            this@MainActivity,
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
                    binding.clRootMain,
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

}