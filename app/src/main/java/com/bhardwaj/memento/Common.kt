package com.bhardwaj.memento

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.Window
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bhardwaj.memento.databinding.FragmentHomeBinding
import com.bhardwaj.memento.fragments.MySingleton
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

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
            binding.downloadButton.isEnabled = false
            binding.cardView.isEnabled = false
            val url = "https://meme-api.herokuapp.com/gimme"

            val request = JsonObjectRequest(
                    Request.Method.GET, url, null, { response ->
                currentMemeUrl = response.getString("url")
                Glide.with(context).load(currentMemeUrl).listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        binding.downloadButton.isEnabled = true
                        binding.cardView.isEnabled = true
                        return false
                    }

                }).into(binding.image)
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