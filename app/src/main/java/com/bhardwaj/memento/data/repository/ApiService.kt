package com.bhardwaj.memento.data.repository

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("gimme")
    suspend fun getRandomMeme(): Response<JsonObject>
}
