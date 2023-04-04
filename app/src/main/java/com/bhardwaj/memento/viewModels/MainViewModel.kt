package com.bhardwaj.memento.viewModels

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bhardwaj.memento.data.repository.MemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val repository: MemeRepository
) : AndroidViewModel(application) {
    var adsCounter: Int = 1
    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String>
        get() = _imageUrl

    fun fetchRandomMeme() {
        viewModelScope.launch {
            _imageUrl.value = repository.getRandomMeme().body()?.get("url")?.asString
        }
    }

    fun getData(list: ArrayList<Any>, from: String = "FAVOURITE") {
        viewModelScope.launch(Dispatchers.IO) {
        }
    }

    fun saveImageToGallery(bitmap: Bitmap, fileLocation: String) {
        viewModelScope.launch(Dispatchers.IO) {
        }
    }
}