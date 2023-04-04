package com.bhardwaj.memento.data.repository

class MemeRepository(private val apiService: ApiService) {
    suspend fun getRandomMeme() = apiService.getRandomMeme()
}