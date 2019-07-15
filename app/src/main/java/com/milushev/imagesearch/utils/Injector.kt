package com.milushev.imagesearch.utils

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.milushev.imagesearch.data.json.JsonMapper
import com.milushev.imagesearch.data.source.DefaultPhotosRepository
import com.milushev.imagesearch.data.source.NetworkPhotosDataSource
import com.milushev.imagesearch.data.source.PhotosRepository
import com.milushev.imagesearch.data.source.ws.WebServiceExecutor
import com.milushev.imagesearch.imageLoad.ImageLoader
import com.milushev.imagesearch.imageLoad.cache.InMemoryCache
import com.milushev.imagesearch.imageLoad.network.NetworkImageDownloader

/**
 * Super simplified service locator implementation to allow us to replace default implementations
 * for testing.
 */
interface Injector {
    companion object {
        private val LOCK = Any()

        private var instance: Injector? = null
        fun instance(context: Context): Injector {
            synchronized(LOCK) {
                if (instance == null) {
                    instance = DefaultInjector(context.applicationContext)
                }
                return instance!!
            }
        }

        /**
         * Allows tests to replace the default implementations.
         */
        @VisibleForTesting
        fun swap(locator: Injector) {
            instance = locator
        }
    }

    fun getRepository(): PhotosRepository

    fun getImageLoader(): ImageLoader

}

/**
 * default implementation of Injector that uses production endpoints.
 */
open class DefaultInjector(private val appContext: Context) : Injector {

    override fun getRepository(): PhotosRepository {
        return DefaultPhotosRepository(
            NetworkPhotosDataSource(
                NetworkUtils(appContext),
                WebServiceExecutor(),
                JsonMapper
            )
        )
    }

    override fun getImageLoader(): ImageLoader {
        return ImageLoader(InMemoryCache(), NetworkImageDownloader())
    }

}