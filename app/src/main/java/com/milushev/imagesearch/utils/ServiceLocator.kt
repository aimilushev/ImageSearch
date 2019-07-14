package com.milushev.imagesearch.utils

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.milushev.imagesearch.data.source.FlickrPhotosRepository
import com.milushev.imagesearch.data.source.NetworkPhotosDataSource
import com.milushev.imagesearch.data.source.PhotosRepository

/**
 * Super simplified service locator implementation to allow us to replace default implementations
 * for testing.
 */
interface ServiceLocator {
    companion object {
        private val LOCK = Any()

        private var instance: ServiceLocator? = null
        fun instance(context: Context): ServiceLocator {
            synchronized(LOCK) {
                if (instance == null) {
                    instance = DefaultServiceLocator(context.applicationContext)
                }
                return instance!!
            }
        }

        /**
         * Allows tests to replace the default implementations.
         */
        @VisibleForTesting
        fun swap(locator: ServiceLocator) {
            instance = locator
        }
    }

    fun getRepository(): PhotosRepository

}

/**
 * default implementation of ServiceLocator that uses production endpoints.
 */
open class DefaultServiceLocator(private val appContext: Context) : ServiceLocator {

    override fun getRepository(): PhotosRepository {
        return FlickrPhotosRepository(NetworkPhotosDataSource(NetworkUtils(appContext)))
    }

}