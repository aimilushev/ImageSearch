package com.milushev.imagesearch.imageLoad.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class NetworkImageDownloader : ImageDownloader {

    private val TAG by lazy { NetworkImageDownloader::class.java.simpleName }

    override suspend fun downloadImage(url: String): Bitmap? = withContext(Dispatchers.IO) {
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