package com.milushev.imagesearch.loadimage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import com.milushev.imagesearch.loadimage.cache.InMemoryCache
import kotlinx.coroutines.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

object ImageLoader {

    private val TAG = ImageLoader::class.java.simpleName
    private var imageCache = InMemoryCache()

    fun loadImage(url: String, imageView: ImageView, coroutineScope: CoroutineScope = GlobalScope) {

        //try getting it from cache
        imageCache.get(url)?.let {
            imageView.setImageBitmap(it)
            return
        }

        //remove old image if any
        imageView.setImageDrawable(null)

        //cancel any running download for current imageView
        (imageView.tag as? ImageViewTag)?.let { tag ->
            if (tag.coroutineJob.isActive) {
                tag.coroutineJob.cancel()
            }
        }

        //download image from network
        val job = coroutineScope.launch(Dispatchers.Main) {
            downloadImage(url)?.let { bitmap ->
                if ((imageView.tag as? ImageViewTag)?.url == url) {
                    imageView.setImageBitmap(bitmap)
                }
                imageCache.put(url, bitmap)
            }
        }
        imageView.tag = ImageViewTag(url, job)
    }

    private suspend fun downloadImage(url: String): Bitmap? = withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        try {
            val conn: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
            bitmap = BitmapFactory.decodeStream(conn.inputStream)
            conn.disconnect()
        } catch (mue: MalformedURLException) {
            Log.d(TAG, "Url $url not well formatted")
        } catch (io: IOException) {
            Log.d(TAG, "Could not load image $url")
        }

        return@withContext bitmap
    }
}

private data class ImageViewTag(var url: String, var coroutineJob: Job)