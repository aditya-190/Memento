package com.bhardwaj.memento

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bhardwaj.memento.databinding.ActivityMainBinding
import com.bhardwaj.memento.fragments.CategoryFragment
import com.bhardwaj.memento.fragments.DownloadFragment
import com.bhardwaj.memento.fragments.FavouriteFragment
import com.bhardwaj.memento.fragments.HomeFragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import kotlin.system.exitProcess


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
        supportFragmentManager.beginTransaction().replace(R.id.fragments, HomeFragment()).commit()
    }

    private fun clickListeners() {
        binding.navigation.setOnClickMenuListener {

            val fragment: Fragment = when (it.id) {
                1 -> CategoryFragment()
                2 -> FavouriteFragment()
                3 -> DownloadFragment()
                else -> HomeFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.fragments, fragment).commit()
        }
    }

    fun quitDialog() {
        binding.quit.visibility = View.VISIBLE
        binding.quitHead.visibility = View.VISIBLE
        binding.sad.visibility = View.VISIBLE
        binding.no.visibility = View.VISIBLE
        binding.yes.visibility = View.VISIBLE

        binding.yes.setOnClickListener {
            exitProcess(0)
        }

        binding.no.setOnClickListener {
            binding.quit.visibility = View.GONE
            binding.quitHead.visibility = View.GONE
            binding.sad.visibility = View.GONE
            binding.no.visibility = View.GONE
            binding.yes.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        Common.fullScreen(this.window)
    }

    override fun onBackPressed() {
    }
}