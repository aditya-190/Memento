package com.bhardwaj.memento.fragments

import android.app.Application
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class MementoInstance : Application() {
    private var mRequestQueue: RequestQueue? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    private val requestQueue: RequestQueue?
        get() {
            if (mRequestQueue == null)
                mRequestQueue = Volley.newRequestQueue(applicationContext)
            return mRequestQueue
        }

    fun <T> addToRequestQueue(req: Request<T>?) {
        requestQueue!!.add(req)
    }

    companion object {
        @get:Synchronized
        var instance: MementoInstance? = null
            private set
    }
}