package com.milushev.imagesearch.imageLoad.network

import android.graphics.Bitmap

interface ImageDownloader {

    suspend fun downloadImage(url: String): Bitmap?
}