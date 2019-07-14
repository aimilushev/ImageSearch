package com.milushev.imagesearch.search

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.milushev.imagesearch.GlideRequests
import com.milushev.imagesearch.R
import com.milushev.imagesearch.data.model.Photo
import com.milushev.imagesearch.data.source.ApiConstants

/**
 * A RecyclerView ViewHolder that displays a square photo.
 */
class PhotoViewHolder(view: View, private val glide: GlideRequests) : RecyclerView.ViewHolder(view) {
    private val thumbnail: ImageView = view.findViewById(R.id.thumbnail)
    private var photo: Photo? = null

    init {
        view.setOnClickListener {
            photo?.let {
                val url = getPhotoUrl(it)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                view.context.startActivity(intent)
            }
        }
    }

    fun bind(photo: Photo?) {
        this.photo = photo
        photo?.let {
            glide.load(getPhotoUrl(it))
                .centerCrop()
                .into(thumbnail)
        }
    }

    private fun getPhotoUrl(photo: Photo) = String.format(
        ApiConstants.IMAGE_TEMPLATE_URL,
        photo.farm,
        photo.server,
        photo.id,
        photo.secret
    )

    companion object {
        fun create(parent: ViewGroup, glide: GlideRequests): PhotoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.photo_item, parent, false)
            return PhotoViewHolder(view, glide)
        }
    }
}