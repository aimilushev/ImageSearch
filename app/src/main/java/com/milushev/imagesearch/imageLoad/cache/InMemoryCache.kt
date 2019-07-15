package com.milushev.imagesearch.imageLoad.cache

import android.graphics.Bitmap
import android.util.LruCache

class InMemoryCache : ImageCache {

    private val cache: LruCache<String, Bitmap>

    init {
        val appMaxMemory: Long = Runtime.getRuntime().maxMemory() / 1024
        val cacheSize: Int = (appMaxMemory / 5).toInt()

        cache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String?, bitmap: Bitmap?): Int {
                return (bitmap?.rowBytes ?: 0) * (bitmap?.height ?: 0) / 1024
            }
        }
    }

    override fun put(url: String, bitmap: Bitmap) {
        cache.put(url, bitmap)
    }

    override fun get(url: String): Bitmap? {
        return cache.get(url)
    }

    override fun clear() {
        cache.evictAll()
    }

}