package com.codecampos.thegallery.view.activities

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.codecampos.thegallery.R
import com.codecampos.thegallery.model.GalleryItem

class PreviewActivity : AppCompatActivity(R.layout.preview_activity_main), MediaPlayer.OnPreparedListener {

    private lateinit var previewContainer: ConstraintLayout
    private lateinit var rootConstraint: ConstraintLayout
    private lateinit var videoViewPreview: VideoView
    private lateinit var imageViewPreview: ImageView
    private lateinit var mediaControls: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mediaControls = MediaController(this)

        previewContainer = findViewById(R.id.preview_container)
        imageViewPreview = findViewById(R.id.imageView_preview)
        rootConstraint = findViewById(R.id.root_gallery)
        videoViewPreview = findViewById(R.id.videoView_preview)

        val preview = intent.extras?.getParcelable<GalleryItem>("gallery_item")
        preview?.let {
            if (it.isVideo) {
                showVideoPreview(it)
            } else {
                showImagePreview(it)
            }
        }
    }

    private fun showVideoPreview(galleryItem: GalleryItem) {
        imageViewPreview.visibility = View.GONE
        videoViewPreview.visibility = View.VISIBLE

        mediaControls.setAnchorView(videoViewPreview)
        videoViewPreview.setVideoURI(galleryItem.uri)
        videoViewPreview.requestFocus()
        videoViewPreview.setOnPreparedListener(this)
    }

    private fun showImagePreview(galleryItem: GalleryItem) {
        Glide
            .with(this)
            .load(galleryItem.uri)
            .into(imageViewPreview)
    }

    override fun onDestroy() {
        super.onDestroy()
        videoViewPreview.stopPlayback()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.isLooping = true
        videoViewPreview.start()
    }

}