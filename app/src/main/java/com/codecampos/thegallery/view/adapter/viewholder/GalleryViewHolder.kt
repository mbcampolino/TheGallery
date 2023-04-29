package com.codecampos.thegallery.view.adapter.viewholder

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.codecampos.thegallery.R
import com.codecampos.thegallery.model.GalleryItem
import com.codecampos.thegallery.view.adapter.GalleryAdapter

class GalleryViewHolder(private val view: View,
                        private var listener: GalleryAdapter.GalleryListener
) : RecyclerView.ViewHolder(view) {

    private val imageViewGallery : ImageView = view.findViewById(R.id.imageView_gallery)

    fun load(galleryItem : GalleryItem) {
        Glide
            .with(view.context)
            .load(galleryItem.uri)
            .transform(CenterCrop(), RoundedCorners(view.resources.getDimensionPixelSize(R.dimen.default_26dp)))
            .into(imageViewGallery)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun showPlaceHolderSelected(galleryItem: GalleryItem) {
        view.setOnClickListener {
            listener.previewItem(galleryItem, imageViewGallery)
        }
    }

}