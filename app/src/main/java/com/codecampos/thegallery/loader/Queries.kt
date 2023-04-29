package com.codecampos.thegallery.loader

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.codecampos.thegallery.model.GalleryItem
import com.codecampos.thegallery.utils.AppUtils
import java.util.ArrayList

class Queries(private val context: Context) {

    private fun query(
        contentUri: Uri,
        projection: Array<String>,
        sortByCol: String,
        idCol: String,
        dateTakenCol: String,
        dateModifiedCol: String,
        mimeTypeCol: String,
        orientationCol: String,
        isVideo: Boolean
    ): List<GalleryItem> {

        val data: MutableList<GalleryItem> = ArrayList()
        val cursor = context
            .contentResolver
            .query(contentUri,
                projection,
                null,
                null,
                null)

            ?: return data

        cursor.use { cursor ->
            val idColNum = cursor.getColumnIndexOrThrow(idCol)
            val dateTakenColNum = cursor.getColumnIndexOrThrow(dateTakenCol)
            val dateModifiedColNum = cursor.getColumnIndexOrThrow(dateModifiedCol)
            val mimeTypeColNum = cursor.getColumnIndex(mimeTypeCol)
            val orientationColNum = cursor.getColumnIndexOrThrow(orientationCol)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColNum)
                val dateTaken = cursor.getLong(dateTakenColNum)
                val mimeType = cursor.getString(mimeTypeColNum)
                val dateModified = cursor.getLong(dateModifiedColNum)
                val orientation = cursor.getInt(orientationColNum)
                if (!mimeType.isNullOrEmpty()) {
                    data.add(
                        GalleryItem(
                            mimeType,
                            dateModified,
                            dateTaken,
                            orientation,
                            false,
                            Uri.withAppendedPath(contentUri, id.toString()),
                            isVideo,
                            AppUtils.dateToShow(dateTaken)
                        )
                    )
                }
            }
        }
        return data
    }

    fun queryImages(): List<GalleryItem> {
        return query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            IMAGE_PROJECTION,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATE_MODIFIED,
            MediaStore.Images.ImageColumns.MIME_TYPE,
            MediaStore.Images.ImageColumns.ORIENTATION,
            false
        )
    }

    fun queryVideos(): List<GalleryItem> {
        return query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            VIDEO_PROJECTION,
            MediaStore.Video.VideoColumns.DATE_TAKEN,
            MediaStore.Video.VideoColumns._ID,
            MediaStore.Video.VideoColumns.DATE_TAKEN,
            MediaStore.Video.VideoColumns.DATE_MODIFIED,
            MediaStore.Video.VideoColumns.MIME_TYPE,
            MediaStore.Images.ImageColumns.ORIENTATION,
            true
        )
    }

    companion object {
        private val VIDEO_ORIENTATION_COLUMN =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Video.VideoColumns.ORIENTATION else "0 AS " + MediaStore.Images.ImageColumns.ORIENTATION
        val IMAGE_PROJECTION = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATE_MODIFIED,
            MediaStore.Images.ImageColumns.MIME_TYPE,
            MediaStore.Images.ImageColumns.ORIENTATION
        )
        val VIDEO_PROJECTION = arrayOf(
            MediaStore.Video.VideoColumns._ID,
            MediaStore.Video.VideoColumns.DATE_TAKEN,
            MediaStore.Video.VideoColumns.DATE_MODIFIED,
            MediaStore.Video.VideoColumns.MIME_TYPE,
            VIDEO_ORIENTATION_COLUMN
        )
    }
}