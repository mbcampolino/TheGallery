package com.codecampos.thegallery.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GalleryItem(
    val mimeType: String? = ".jpg", /// jpeg, mp4, mpeg, mp3, wav
    val dateModified: Long = 0,
    val dateTaken: Long = 0,
    val orientation: Int = 0,
    val isHeader: Boolean = false,
    val uri: Uri? = null,
    val isVideo: Boolean = false,
    val dateToShow: String = "Today"): Parcelable

