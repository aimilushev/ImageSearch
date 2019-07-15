package com.milushev.imagesearch.imageLoad

import android.widget.ImageView
import com.milushev.imagesearch.imageLoad.cache.ImageCache
import com.milushev.imagesearch.imageLoad.network.ImageDownloader
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ImageLoader(private val imageCache: ImageCache, private val imageDownloader: ImageDownloader) {

    fun loadImage(
        url: String,
        imageView: ImageView,
        coroutineScope: CoroutineScope = GlobalScope,
        uiDispatcher: CoroutineContext = Dispatchers.Main
    ) {

        //try getting it from cache
        imageCache.get(url)?.let {
            imageView.setImageBitmap(it)
            return
        }

        //remove old image if any
        imageView.setImageBitmap(null)

        //cancel any running download for current imageView
        (imageView.tag as? ImageViewTag)?.let { tag ->
            if (tag.coroutineJob.isActive) {
                tag.coroutineJob.cancel()
            }
        }

        //download image from network
        val job = coroutineScope.launch(uiDispatcher) {
            imageDownloader.downloadImage(url)?.let { bitmap ->
                if ((imageView.tag as? ImageViewTag)?.url == url) {
                    imageView.setImageBitmap(bitmap)
                }
                imageCache.put(url, bitmap)
            }
        }
        imageView.tag = ImageViewTag(url, job)
    }


}

data class ImageViewTag(var url: String, var coroutineJob: Job)