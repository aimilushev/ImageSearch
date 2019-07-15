package com.milushev.imagesearch.search

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.milushev.imagesearch.data.model.Photo
import com.milushev.imagesearch.imageLoad.ImageLoader
import kotlinx.coroutines.CoroutineScope

class PhotosAdapter(private val imageLoader: ImageLoader, private val imageDownloadScope: CoroutineScope) :
    PagedListAdapter<Photo, PhotoViewHolder>(PHOTO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PhotoViewHolder.create(parent, imageLoader, imageDownloadScope)

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {

        val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<Photo>() {
            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean =
                oldItem.id == newItem.id
        }

    }
}