package com.bhardwaj.memento.viewModels

import android.app.Application
import android.content.ContentUris
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bhardwaj.memento.MementoApplication
import com.bhardwaj.memento.data.entity.Downloads
import com.bhardwaj.memento.data.entity.Favourites
import com.bhardwaj.memento.data.repository.MemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MemeRepository, application: Application,
) : AndroidViewModel(application) {
    var adsCounter: Int = 1

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String>
        get() = _imageUrl

    private val _imageListFromDownloads = MutableLiveData<ArrayList<Downloads>>()
    val imageListFromDownloads: LiveData<ArrayList<Downloads>>
        get() = _imageListFromDownloads

    private val _imageListFromFavourites = MutableLiveData<ArrayList<Favourites>>()
    val imageListFromFavourites: LiveData<ArrayList<Favourites>>
        get() = _imageListFromFavourites

    fun fetchRandomMeme() {
        viewModelScope.launch {
            _imageUrl.value = repository.getRandomMeme().body()?.get("url")?.asString
        }
    }

    fun getData(from: String = "FAVOURITE") {
        viewModelScope.launch(Dispatchers.IO) {
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATA
            )

            var selection = "${MediaStore.Images.Media.DISPLAY_NAME} LIKE 'MEMENTO_%'"
            selection += " AND ${MediaStore.Images.Media.DISPLAY_NAME} LIKE '%_${from}_%'"

            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

            val temporaryFavouriteList = ArrayList<Favourites>()
            val temporaryDownloadList = ArrayList<Downloads>()

            getApplication<MementoApplication>().contentResolver.query(
                collection,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ContentUris.withAppendedId(
                            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                            id
                        )
                    } else {
                        val path =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                        Uri.fromFile(File(path))
                    }
                    if (from == "FAVOURITE") temporaryFavouriteList.add(Favourites(uri))
                    else temporaryDownloadList.add(Downloads(uri))
                }
            } ?: arrayListOf<Any>()

            if (from == "FAVOURITE") _imageListFromFavourites.postValue(temporaryFavouriteList)
            else _imageListFromDownloads.postValue(temporaryDownloadList)
        }
    }

    fun saveImageToGallery(bitmap: Bitmap?, fileType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val fileName = "Memento_${fileType}_${System.currentTimeMillis()}.jpeg"
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.WIDTH, bitmap?.width)
                put(MediaStore.Images.Media.HEIGHT, bitmap?.height)
            }
            try {
                getApplication<MementoApplication>().contentResolver.insert(
                    collection,
                    contentValues
                )?.also { uri ->
                    getApplication<MementoApplication>().contentResolver.openOutputStream(uri)
                        .use { outputStream ->
                            if (bitmap != null) {
                                outputStream?.let {
                                    if (!bitmap.compress(
                                            Bitmap.CompressFormat.JPEG,
                                            100,
                                            it
                                        )
                                    ) {
                                        throw IOException("Couldn't save bitmap.")
                                    }
                                }
                            }
                        }
                } ?: throw IOException("Couldn't create MediaStore entry.")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}