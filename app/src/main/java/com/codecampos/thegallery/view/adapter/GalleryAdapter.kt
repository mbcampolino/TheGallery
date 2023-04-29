package com.codecampos.thegallery.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecampos.thegallery.R
import com.codecampos.thegallery.model.GalleryItem
import com.codecampos.thegallery.view.adapter.viewholder.GalleryHeaderViewHolder
import com.codecampos.thegallery.view.adapter.viewholder.GalleryViewHolder

class GalleryAdapter(
    var galleryItems: ArrayList<GalleryItem>,
    var listener: GalleryListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val HEADER = 0
        const val MEDIA = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder  {
        if (viewType == HEADER) {
            return GalleryHeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_gallery_header, parent, false))
        }
        return GalleryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false), listener)
    }

    override fun getItemViewType(position: Int) = if (galleryItems[position].isHeader) HEADER else MEDIA

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val galleryItem = galleryItems[position]
        if (getItemViewType(position) == HEADER) {
            (holder as GalleryHeaderViewHolder).setTitle(galleryItem.dateToShow)
        } else if (getItemViewType(position) == MEDIA) {
            (holder as GalleryViewHolder).load(galleryItem)
            holder.showPlaceHolderSelected(galleryItem)
        }
    }

    override fun getItemCount() = galleryItems.size

    interface GalleryListener {
        fun previewItem(galleryItem: GalleryItem, view: View) : Boolean
    }
}