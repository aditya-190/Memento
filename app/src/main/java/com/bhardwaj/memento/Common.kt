package com.bhardwaj.memento

import android.content.Context
import android.os.Build
import android.view.View
import android.view.Window
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bhardwaj.memento.databinding.FragmentHomeBinding
import com.bhardwaj.memento.fragments.MySingleton
import com.bumptech.glide.Glide

class Common {
    companion object {
        lateinit var currentMemeUrl: String

        fun fullScreen(window: Window) {
            window.decorView.apply {
                systemUiVisibility = if (Build.VERSION.SDK_INT in 12..18) View.GONE
                else
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }
        }

        fun fetchRandomMeme(binding: FragmentHomeBinding, context: Context) {
            binding.progressBar.visibility = View.VISIBLE
            val url = "https://meme-api.herokuapp.com/gimme"

            val request = JsonObjectRequest(
                    Request.Method.GET, url, null, { response ->
                currentMemeUrl = response.getString("url")
                Glide.with(context).load(currentMemeUrl).into(binding.image)
                binding.progressBar.visibility = View.GONE
            }, {
                binding.progressBar.visibility = View.GONE
            })
            MySingleton.getInstance(context).addToRequestQueue(request)
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
}