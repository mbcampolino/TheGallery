package com.codecampos.thegallery.view.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.MemoryCategory
import com.codecampos.thegallery.R
import com.codecampos.thegallery.loader.MediaStoreDataLoader
import com.codecampos.thegallery.model.GalleryItem
import com.codecampos.thegallery.view.adapter.GalleryAdapter
import com.codecampos.thegallery.view.adapter.design.GridGallery
import java.util.*

class MainActivity : AppCompatActivity(R.layout.gallery_activity_main), GalleryAdapter.GalleryListener, LoaderManager.LoaderCallbacks<List<GalleryItem>>, View.OnClickListener {

    private lateinit var galleryList: RecyclerView
    lateinit var btnImportMedias: Button
    lateinit var btnOrderBy: Button
    private lateinit var btnFilterContentType: Button
    private lateinit var btnPermission: Button
    private lateinit var progressBar: ProgressBar
    lateinit var galleryAdapter: GalleryAdapter

    lateinit var rootConstraint: ConstraintLayout

    var orderDesc = true
    private var filterContentType = arrayListOf("videos and pics", "only videos", "only pics")
    var currentFilterContent = 0

    /// salva para fazer filtro e recarregar so quando necessario
    var lastFilterApplied = 0
    var lastOrder = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH)
        rootConstraint = findViewById(R.id.root_gallery)
        progressBar = findViewById(R.id.progressBar_view)
        galleryList = findViewById(R.id.recycler_gallery)
        btnImportMedias = findViewById(R.id.button_select_medias)
        btnOrderBy = findViewById(R.id.button_orderby)
        btnOrderBy.setOnClickListener(this)
        btnFilterContentType = findViewById(R.id.button_type_media)
        btnFilterContentType.setOnClickListener(this)
        btnPermission = findViewById(R.id.button_request_media_access)
        btnPermission.setOnClickListener(this)

        galleryAdapter = GalleryAdapter(arrayListOf(), this)
        galleryList.adapter = galleryAdapter

        (galleryList.layoutManager as GridLayoutManager).spanSizeLookup = GridGallery(galleryList)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            btnPermission.visibility = View.VISIBLE
        } else {
            initLoadMedia()
        }
    }

    private fun loadingView(showLoading: Boolean) {
        TransitionManager.beginDelayedTransition(rootConstraint, Fade())
        progressBar.visibility = if (showLoading) View.VISIBLE else View.GONE
        galleryList.visibility = if (showLoading) View.INVISIBLE else View.VISIBLE
    }

    private fun initLoadMedia() {
        loadingView(true)
        LoaderManager.getInstance(this).initLoader(0,null,this);
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1001 -> {
                if (grantResults.isNotEmpty() && grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                    initLoadMedia()
                } else {
                    Toast.makeText(this, "Storage permission is required", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadMedia(loadedMedia: ArrayList<GalleryItem>) {
        btnPermission.visibility = View.GONE
        if (galleryAdapter.galleryItems.isNullOrEmpty() or (currentFilterContent != lastFilterApplied) or (lastOrder != orderDesc)) {
            lastFilterApplied = currentFilterContent
            lastOrder = orderDesc
            galleryAdapter.galleryItems = loadedMedia
            galleryAdapter.notifyDataSetChanged()
            loadingView(false)
        }
    }

    override fun previewItem(galleryItem: GalleryItem, view: View) : Boolean {

        val options = ViewCompat.getTransitionName(view)?.let {
            ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, it)}

        startActivity(Intent(this, PreviewActivity::class.java).apply {
            putExtra("gallery_item", galleryItem)
        }, options?.toBundle())

        return true
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<GalleryItem>> {
        return MediaStoreDataLoader(this, orderDesc, currentFilterContent)
    }

    override fun onLoadFinished(loader: Loader<List<GalleryItem>>, data: List<GalleryItem>?) {
        loadMedia(data as ArrayList<GalleryItem>)
    }

    override fun onLoaderReset(loader: Loader<List<GalleryItem>>) {
        // Do nothing
    }

    override fun onClick(view: View?) {

        when(view) {
            btnPermission -> ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1001)

            btnOrderBy -> {
                orderDesc =! orderDesc
                if (orderDesc) {
                    btnOrderBy.text = getString(R.string.new_files_first)
                } else {
                    btnOrderBy.text = getString(R.string.old_files_first)
                }
                loadingView(true)

                supportLoaderManager.restartLoader(R.id.loader_id_media_store_data, null, this)
            }

            btnFilterContentType -> {
                if (currentFilterContent < filterContentType.size - 1) currentFilterContent+=1 else currentFilterContent=0
                btnFilterContentType.text = filterContentType[currentFilterContent]
                supportLoaderManager.restartLoader(R.id.loader_id_media_store_data, null, this)
            }
        }
    }

}