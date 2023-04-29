package com.codecampos.thegallery.view.adapter.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codecampos.thegallery.R

class GalleryHeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun setTitle(date : String) {
        view.findViewById<TextView>(R.id.title_header).text = date
    }

}