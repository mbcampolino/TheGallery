package com.codecampos.thegallery.view.adapter.design

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codecampos.thegallery.view.adapter.GalleryAdapter

class GridGallery(val galleryList: RecyclerView) : GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        return if ((galleryList.adapter as GalleryAdapter)
                .galleryItems[position].isHeader) (galleryList.layoutManager as GridLayoutManager).spanCount else 1
    }
}