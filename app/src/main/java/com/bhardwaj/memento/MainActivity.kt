package com.bhardwaj.memento

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bhardwaj.memento.databinding.ActivityMainBinding
import com.bhardwaj.memento.fragments.CategoryFragment
import com.bhardwaj.memento.fragments.DownloadFragment
import com.bhardwaj.memento.fragments.FavouriteFragment
import com.bhardwaj.memento.fragments.HomeFragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialise()
        clickListeners()
    }

    private fun initialise() {
        binding.navigation.apply {
            add(MeowBottomNavigation.Model(0, R.drawable.icon_home))
            add(MeowBottomNavigation.Model(1, R.drawable.icon_category))
            add(MeowBottomNavigation.Model(2, R.drawable.icon_favourite))
            add(MeowBottomNavigation.Model(3, R.drawable.icon_download))
            show(0)
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragments, HomeFragment())
            .addToBackStack(null).commit()
    }

    private fun clickListeners() {
        binding.navigation.setOnClickMenuListener {
            when (it.id) {
                1 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments, CategoryFragment())
                        .addToBackStack(null).commit()
                }
                2 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments, FavouriteFragment())
                        .addToBackStack(null).commit()
                }
                3 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments, DownloadFragment())
                        .addToBackStack(null).commit()
                }
                else -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments, HomeFragment())
                        .addToBackStack(null).commit()
                }
            }
        }
    }

    private fun fullScreen() {
        if (Build.VERSION.SDK_INT in 12..18) this.window.decorView.systemUiVisibility = View.GONE
        else window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    override fun onResume() {
        super.onResume()
        fullScreen()
    }
}