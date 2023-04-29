package com.codecampos.thegallery.loader

import android.provider.MediaStore
import android.content.Context
import androidx.loader.content.AsyncTaskLoader
import com.codecampos.thegallery.model.GalleryItem
import java.util.ArrayList

class MediaStoreDataLoader internal constructor(context: Context, var orderDesc: Boolean, var currentFilterContent: Int) : AsyncTaskLoader<List<GalleryItem>>(context) {

    private var cached: List<GalleryItem>? = null
    private var observerRegistered = false
    private val forceLoadContentObserver: ForceLoadContentObserver = ForceLoadContentObserver()

    override fun deliverResult(data: List<GalleryItem>?) {
        super.deliverResult(data)
        if (!isReset && isStarted) {
            super.deliverResult(data)
        }
    }

    override fun onStartLoading() {
        if (cached != null) {
            deliverResult(cached)
        }
        if (takeContentChanged() || cached == null) {
            forceLoad()
        }
        registerContentObserver()
    }

    override fun onStopLoading() {
        cancelLoad()
    }

    override fun onReset() {
        super.onReset()
        onStopLoading()
        cached = null
        unregisterContentObserver()
    }

    override fun onAbandon() {
        super.onAbandon()
        unregisterContentObserver()
    }

    override fun loadInBackground(): List<GalleryItem> {
        val unformattedList: MutableList<GalleryItem> = ArrayList()

        when (currentFilterContent) {
            ALL_FILES -> {
                unformattedList.addAll(Queries(context).queryImages())
                unformattedList.addAll(Queries(context).queryVideos())
            }
            VIDEOS -> {
                unformattedList.addAll(Queries(context).queryVideos())
            }
            else -> {
                unformattedList.addAll(Queries(context).queryImages())
            }
        }

        for (i in unformattedList.indices) {
            if (i == HEADER) {
                unformattedList.add(0, GalleryItem(isHeader = true,
                    dateToShow = unformattedList.first().dateToShow))
            } else {
                val lastGalleryItem = unformattedList[i - HEADER]
                val galleryItem = unformattedList[i]
                if (galleryItem.dateToShow != lastGalleryItem.dateToShow) {
                    unformattedList.add(i, GalleryItem(isHeader = true,
                        dateToShow = galleryItem.dateToShow))
                }
            }
        }
        return orderBy(unformattedList)
    }

    private fun orderBy(unformattedList: MutableList<GalleryItem>) : MutableList<GalleryItem> {
        if (orderDesc) {
            unformattedList.sortByDescending { it.dateTaken }
        } else {
            unformattedList.sortBy { it.dateTaken }
        }
        return unformattedList
    }

    private fun registerContentObserver() {
        if (!observerRegistered) {
            val cr = context.contentResolver
            cr.registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, forceLoadContentObserver
            )
            cr.registerContentObserver(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, false, forceLoadContentObserver
            )
            observerRegistered = true
        }
    }

    private fun unregisterContentObserver() {
        if (observerRegistered) {
            observerRegistered = false
            context.contentResolver.unregisterContentObserver(forceLoadContentObserver)
        }
    }

    companion object {
        const val ALL_FILES = 0
        const val VIDEOS = 1
        const val HEADER = 0
    }
}